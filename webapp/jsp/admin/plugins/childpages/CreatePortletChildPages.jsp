<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="portletChildPages" scope="session" class="fr.paris.lutece.plugins.childpages.web.portlet.ChildPagesPortletJspBean" />


<% portletChildPages.init( request,  portletChildPages.RIGHT_MANAGE_ADMIN_SITE  ); %>
<%= portletChildPages.getCreate( request ) %>

<%@ include file="../../AdminFooter.jsp" %>

