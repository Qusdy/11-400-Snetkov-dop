<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <title>SpaceX</title>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
</head>
<body>

<h3>SpaceX launches</h3>

<div>
    <#if nextLaunch??>
        Next: ${nextLaunch.name!""?html} | ${nextLaunch.date_utc!""?html}
    <#else>
        Next: no data (press load)
    </#if>
</div>

<hr>

<div>
    <select id="year">
        <option value="">year=all</option>
        <#list 2006..2025 as y>
            <option value="${y?c}">${y?c}</option>
        </#list>
    </select>

    <select id="status">
        <option value="all">status=all</option>
        <option value="success">status=success</option>
        <option value="failure">status=failure</option>
    </select>

    <select id="rocket">
        <option value="">rocket=all</option>
        <option value="Falcon 1">Falcon 1</option>
        <option value="Falcon 9">Falcon 9</option>
        <option value="Falcon Heavy">Falcon Heavy</option>
    </select>

    <input id="q" placeholder="mission name">
    <button id="apply">load last 20</button>
    <span id="meta"></span>
</div>

<hr>

<div id="grid">loading...</div>

<dialog id="detailDlg">
    <h4 id="detailTitle"></h4>
    <div id="detailBody"></div>
    <form method="dialog"><button>close</button></form>
</dialog>

<script>
    function esc(s) {
        return String(s || '')
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
    }

    function getFilters() {
        return {
            year: $('#year').val(),
            status: $('#status').val(),
            rocket: $('#rocket').val(),
            q: $('#q').val(),
            limit: 20
        };
    }

    function statusText(success) {
        if (success === true) return 'SUCCESS';
        if (success === false) return 'FAIL';
        return 'N/A';
    }

    function renderLaunch(l) {
        const youtube = l.links && l.links.youtube_id
            ? ('<a target="_blank" href="https://www.youtube.com/watch?v=' + esc(l.links.youtube_id) + '">video</a>')
            : '';

        return (
            '<div style="border:1px solid #ddd;padding:8px;margin:8px 0">' +
            '<div><b>' + esc(l.name) + '</b></div>' +
            '<div>date: ' + esc(l.date_utc) + '</div>' +
            '<div>rocket: ' + esc(l.rocketName || '') + '</div>' +
            '<div>status: ' + esc(statusText(l.success)) + '</div>' +
            '<div>' + youtube + '</div>' +
            '<button class="detailBtn" data-id="' + esc(l.id) + '">detail</button>' +
            '</div>'
        );
    }

    function ensureLoaded(cb) {
        $('#meta').text(' loading all launches/rockets...');
        $.ajax({
            url: '/api/launches/load',
            method: 'POST',
            dataType: 'json',
            success: function(info) {
                $('#meta').text(' loaded launches=' + info.launchCount + ', rockets=' + info.rocketCount);
                cb();
            },
            error: function(xhr) {
                $('#meta').text(' load error: ' + xhr.status);
            }
        });
    }

    function loadLaunches() {
        $('#grid').text('loading...');
        $.ajax({
            url: '/api/launches',
            method: 'GET',
            dataType: 'json',
            data: getFilters(),
            success: function(list) {
                let html = '';
                for (let i = 0; i < list.length; i++) html += renderLaunch(list[i]);
                $('#grid').html(html);

                $('.detailBtn').on('click', function() {
                    loadDetail($(this).data('id'));
                });
            },
            error: function(xhr) {
                $('#grid').text('error: ' + xhr.status + ' ' + (xhr.responseText || ''));
            }
        });
    }

    function loadDetail(id) {
        $.ajax({
            url: '/api/launch',
            method: 'GET',
            dataType: 'json',
            data: { id: id },
            success: function(l) {
                $('#detailTitle').text(l.name || 'Launch');
                const youtube = l.links && l.links.youtube_id
                    ? ('<a target="_blank" href="https://www.youtube.com/watch?v=' + esc(l.links.youtube_id) + '">youtube</a>')
                    : '';
                $('#detailBody').html(
                    '<div>date: ' + esc(l.date_utc || '') + '</div>' +
                    '<div>rocket: ' + esc(l.rocketName || '') + '</div>' +
                    '<div>success: ' + esc(String(l.success)) + '</div>' +
                    '<div>details: ' + esc(l.details || '') + '</div>' +
                    '<div>' + youtube + '</div>'
                );
                document.getElementById('detailDlg').showModal();
            },
            error: function(xhr) {
                alert('detail error: ' + xhr.status);
            }
        });
    }

    $('#apply').on('click', function() { loadLaunches(); });
    ensureLoaded(loadLaunches);
</script>

</body>
</html>
