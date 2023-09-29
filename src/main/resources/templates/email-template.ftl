 <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style>
.table1{
border-collapse: collapse;

width: 300px;
margin-right: auto;
}
.key{
    font-size: small;
    color: black;
    background-color: rgb(171, 223, 251);
}
.value{
    
    text-align:center;
    font-size: small;
    color: black;
}
.heding{
    padding-left: 80px;
    color: rgb(100, 91, 91);
}
.tbl2head{
    font-size: x-small;
    background-color: rgb(171, 223, 251);
    color:black;
    text-align: center;
    padding: 10px;
}
td {
  border-bottom: 1px solid #ddd;
  padding: 10px;
  text-align: left;
}
</style>
<title>Appraisal Mail</title>
</head>
<body>
    <p style="font-family:verdana",color="black,font-size="50px">Dear Sir/Madam,</p>
    <p style="font-family:verdana",color="black,font-size="50px">Your Vehicle Has been appraised successfully.</p>
<table class="table1" border="1">

<tr>
<td class="key"><b>Name</b></td>
<td class="value">${Name}</td>
</tr>
<tr>
<td class="key"><b>Vin Number</b></td>
<td class="value">${Vin}</td>
</tr>
<tr>
<td class="key"><b>Year</b> </td>
<td class="value">${vehYr}</td>
</tr>
<tr>
<td class="key"><b>Vehicle Series</b></td>
<td class="value">${vehSer}</td>
</tr>
<tr>
<td class="key"><b>Vehicle Model</b></td>
<td class="value">${vehMod}</td>
</tr>
<tr>
    <td class="key"><b>Vehicle Mileage</b></td>
    <td class="value">${vehMil}</td>
</tr>
<tr>
    <td class="key"><b>Vehicle Make</b></td>
    <td class="value">${vehMake}</td>
</tr>
<tr>
    <td class="key"><b>Appraisal Value</b></td>
    <td class="value">${appVal}</td>
</tr>
</table>
<p></p>
&nbsp;

<table class="table1" border="1">

   <tr>
    <th class="tbl2head"><b>Front Driver Side Damage Status</b></th>
    <th class="tbl2head"><b>Rear Driver Side Damage Status</b></th>
    <th class="tbl2head"><b>Rear Passenger Side Damage Status</b></th>
    <th class="tbl2head"><b>Front Passenger Side Damage Status</b></th>
    <th class="tbl2head"><b>Front Driver Side paintWork Status</b></th>
    <th class="tbl2head"><b>Rear Driver Side paintWork Status</b></th>
    <th class="tbl2head"><b>Front Passenger Side paintWork Status</b></th>
    <th class="tbl2head"><b>Rear Passenger Side paintWork Status</b></th>
</tr>

<tr class="hover2">
    <td class="tblData">${frDrSdDgSts}</td>
    <td class="tblData">${rrDrSdDgSts}</td>
    <td class="tblData">${rrPsSdDgSts}</td>
    <td class="tblData">${frPsSdDgSts}</td>
    <td class="tblData">${frDrSdPaWkSts}</td>
    <td class="tblData">${rrDrSdPaWkSts}</td>
    <td class="tblData">${frPsSdPaWkSts}</td>
    <td class="tblData">${rrPsSdPaWkSts}</td>
</tr>
    </table>
    <p></p>
    &nbsp;
<p><b>Thanks & Regards</b></p>
</body>
</html>

