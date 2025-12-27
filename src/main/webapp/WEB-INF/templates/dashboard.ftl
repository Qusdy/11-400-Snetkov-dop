<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <title>SpaceX Dashboard</title>
    <style>
        table { border-collapse: collapse; }
        td, th { border: 1px solid #ddd; padding: 6px; }
        .muted { color:#777; }
        .ok { color: #0a7; }
        .bad { color: #c33; }
    </style>
</head>
<body>

<h3>Dashboard</h3>

<form method="post" action="/dashboard/refresh">
    <button type="submit">Full refresh (reload launches + rockets)</button>
</form>

<hr>

<#if loaded?? && loaded>
    <div class="muted">
        Loaded at (ms): ${loadedAtMs?c}
    </div>

    <h4>Stats</h4>
    <div>Success %: ${stats.successPct?c}</div>
    <div>Known results: ${stats.totalWithKnownResult?c}</div>

    <h4>Launches by year</h4>
    <table>
        <tr><th>Year</th><th>Count</th></tr>
        <#list stats.launchesByYear?keys as y>
            <tr>
                <td>${y?c}</td>
                <td>${stats.launchesByYear[y]?c}</td>
            </tr>
        </#list>
    </table>

    <h4>Timeline (showing last ${timeline?size?c} of ${timelineTotal?c})</h4>

    <#list timeline as l>
        <div>
            ${l.dateUtc!""?html} |
            ${l.name!""?html} |
            ${l.rocketName!""?html} |
            <#if l.success??>
                <#if l.success>
                    <span class="ok">SUCCESS</span>
                <#else>
                    <span class="bad">FAIL</span>
                </#if>
            <#else>
                N/A
            </#if>
        </div>
    </#list>

<#else>
    <div class="muted">No data loaded yet. Press Full refresh.</div>
</#if>

</body>
</html>
