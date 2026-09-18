<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.childpages.web.portlet.ChildPagesPortletJspBean"%>

${ childPagesPortletJspBean.init( pageContext.request, ChildPagesPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( childPagesPortletJspBean.doCreate( pageContext.request )) }
