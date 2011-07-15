<html>
<%
if (request.getParameter("name") == null) {
%>
<form>
  <input name="name">
  <input type="submit">
</form>
<%
}
else {
%>
hello, <%=request.getParameter("name")%>
<%
}
%>
</html>
