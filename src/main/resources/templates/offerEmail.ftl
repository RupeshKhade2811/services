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
<title>Offer Mail</title>
</head>
<body>
    <p style="font-family:verdana",color="black,font-size="50px">Dear Sir/Madam,</p>
    <p style="font-family:verdana",color="black,font-size="50px">${message}</p>
<table class="table1" border="1">

<tr>
<td class="key"><b>Seller Name</b></td>
<td class="value">${Name1}</td>
</tr>
<tr>
<td class="key"><b>Vin Number</b></td>
<td class="value">${Vin}</td>
</tr>
<td class="key"><b>Price</b></td>
<td class="value">${price}</td>
</tr>

</table>
<p></p>
&nbsp;
<p></p>
&nbsp;
<p><b>Thanks & Regards</b></p>
</body>
</html>

