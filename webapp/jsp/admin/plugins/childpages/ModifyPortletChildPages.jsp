<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.childpages.web.portlet.ChildPagesPortletJspBean"%>

${ childPagesPortletJspBean.init( pageContext.request, ChildPagesPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ childPagesPortletJspBean.getModify( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
