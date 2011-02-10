<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="portletChildPages" scope="session" class="fr.paris.lutece.plugins.childpages.web.portlet.ChildPagesPortletJspBean" />


<%
    portletChildPages.init( request,   portletChildPages.RIGHT_MANAGE_ADMIN_SITE  );
    response.sendRedirect(  portletChildPages.doCreate( request )    );
%>
