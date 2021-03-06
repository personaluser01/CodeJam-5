<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Stats</title>

<style type="text/css">

table
{
border-collapse:collapse;
}
table, th, td
{
border: 1px solid black;
}
td
{
padding:4px;
}
th
{
background-color:green;
color:white;
verdana, helvetica, arial, sans-serif;
}

.playerTable
{
    margin : 10px 0px 10px 0px;
}

</style>

<script type="text/javascript" src="jquery.min.js"></script>

<!-- CSS file -->
<link type="text/css" rel="stylesheet" href="jquery.qtip.css" />


<script>

$(document).ready(function() {


$('.tooltip').qtip({ // Grab some elements to apply the tooltip to
    content: true,
     position: {
        my: 'top left',  // Position my top left...
        at: 'center', // at the bottom right of...
        target: 'event' // my target
    },
    style: {
        width: '800px'
    },
    hide: {
            // event: 'click',
             //inactive: 1200,
             fixed: true
         }
    /*position: {
        viewport: $(window)
    }*/
});           

<#assign actionIds = [0,1,2,3,4,5,6,7,8]>
<#assign posList = [0, 1, 4, 3, 2]>


});
</script>

</head>

<body>

<script type="text/javascript" src="jquery.qtip.min.js"></script>



  <!--${"\x6464"}-->
  
    
    <#list stats.currentPlayerList as playerName>
     <#assign pStat=stats.playerSessionStats[playerName]/>
    <h1> ${playerName}  ( hands : ${pStat.totalHands} )</h1>
    <table class="playerTable">
    
    
    <tr><td>Agg. Freq ${pStat.stats.agg.getPerc()}</td><td class="tooltip" title="${pStat.stats["3bet"].getActionsDesc()}"  >${pStat.stats["3bet"]}</td></tr>
    <tr><td>Went to showdown ${pStat.stats.wtsd.getWTSD()}</td><td>Won at showdown ${pStat.stats.wtsd.getWonAtSD()}</td></tr>
    </table>
    
    <table>
        <tr>
            <th></th>
            <th>Small blind</th>
            <th>Big blind</th>
            <th>Early position (btn-2)</th>
            <th>Mid position (btn-1)</th>
            <th>Button</th>
        </tr>
        
    
        
        <tr>
            <td>${pStat.stats.vpip}</td>
            <#list posList as pos> 
            <td>${pStat.stats.vpip.getPercentage(pos)}</td>
            </#list>            
        </tr>
        
        <tr>
            <td>${pStat.stats.notfpfr}</td>
            <#list posList as pos> 
            <td class="tooltip" title='${pStat.stats.notfpfr.getActionsDesc(pos)}' id="notfpfr_${playerName_index}_${pos}">${pStat.stats.notfpfr.getPercentage(pos)}</td>
            </#list>  
        </tr>
        
        <tr>
            <td>${pStat.stats.pfr}</td>
            <#list posList as pos> 
            <td class="tooltip" title='${pStat.stats.pfr.getActionsDesc(pos)}' id="pfr_${playerName_index}_${pos}">${pStat.stats.pfr.getPercentage(pos)}</td>
            </#list>  
        </tr>
            
    </table>
    
    <table >
    <#assign seq = ["dcl1", "dcl2", "dcl3"]>
    
    <#assign actionIds = [1,0,4,2,7,6,5,3]>
    <#assign types = [0, 1, 2]>
    <#list seq as dcl>
        <tr>
            <th>Type</th>
            <th>Count</th>
            <th>Bet</th>
            <th>Call</th>
            <th>Raise</th>
            <th>Fold to bet</th>
            <th>Fold to raise</th>
            <th>Check/raise</th>
            <th>Reraise</th>
            <th>Allin</th>
        </tr>
            <tr>
                <td>Cumul</td>
                <td >${pStat.stats[dcl].getCount(0)+pStat.stats[dcl].getCount(1)+pStat.stats[dcl].getCount(2)}</td>
                <#list actionIds as actionId>
                <td >${pStat.stats[dcl].getActionStr(actionId)}</td>
                
                </#list>
            </tr>
        <#list types as type> 
            <tr>
            
            <#assign idPrefix = "player_${playerName_index}_round_${dcl_index}_type_${type}">
                <td>${pStat.stats[dcl].getTypeStr(type)}</td>
                <td >${pStat.stats[dcl].getCount(type)}</td>
            <#list actionIds as actionId>
                <td class="tooltip" id="${idPrefix}_action${actionId}" 
                title='${pStat.stats[dcl].getActionDesc(type, actionId)}'>${pStat.stats[dcl].getActionStr(type, actionId)}</td>
                
            </#list>
            </tr>
        </#list>
    </#list>
    </table>  
</#list>
  
<div style="height: 800px"/>

</body>

</html>
