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
        Next: ${nextLaunch.name!""?html} | ${nextLaunch.dateUtc!""?html}
    <#else>
        Next: no data
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
    <button id="apply">load</button>
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
            q: $('#q').val()
        };
    }

    function rocketName(l) {
        if (!l.rocket) return '';
        if (typeof l.rocket === 'string') return l.rocket;
        return l.rocket.name || '';
    }

    function statusText(success) {
        if (success === true) return 'SUCCESS';
        if (success === false) return 'FAIL';
        return 'N/A';
    }

    function renderLaunch(l) {
        return (
            '<div>' +
            '<b>' + esc(l.name) + '</b>' +
            ' | ' + esc(l.dateUtc) +
            ' | ' + esc(rocketName(l)) +
            ' | ' + esc(statusText(l.success)) +
            ' <button class="detailBtn" data-id="' + esc(l.id) + '">detail</button>' +
            '</div><hr>'
        );
    }

    function loadLaunches() {
        $('#grid').text('loading...');

        $.ajax({
            url: '/api/launches',
            method: 'GET',
            dataType: 'json',
            data: getFilters(),
            success: function(data) {
                var docs = (data && data.docs) ? data.docs : [];
                var html = '';

                for (var i = 0; i < docs.length; i++) {
                    html += renderLaunch(docs[i]);
                }

                $('#grid').html(html);

                $('.detailBtn').on('click', function() {
                    var id = $(this).data('id');
                    loadDetail(id);
                });
            },
            error: function(xhr, status, err) {
                $('#grid').text('error: ' + status);
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
                $('#detailBody').html(
                    '<div>date: ' + esc(l.dateUtc || '') + '</div>' +
                    '<div>success: ' + esc(String(l.success)) + '</div>' +
                    '<div>details: ' + esc(l.details || '') + '</div>' +
                    '<div>rocket: ' + esc(rocketName(l)) + '</div>'
                );
                document.getElementById('detailDlg').showModal();
            },
            error: function(xhr, status, err) {
                alert('detail error: ' + status);
            }
        });
    }

    $('#apply').on('click', loadLaunches);
    loadLaunches();
</script>

</body>
</html>
