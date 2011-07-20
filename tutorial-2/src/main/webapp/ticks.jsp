<%@ page import="foo.*"%>
<html>
<%
TickDAO tickDAO = new TickDAO();
tickDAO.insertTick();
%>
<%=tickDAO.getTickCount()%> Ticks
</html>
