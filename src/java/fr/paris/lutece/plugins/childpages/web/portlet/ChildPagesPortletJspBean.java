/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.childpages.web.portlet;

import fr.paris.lutece.plugins.childpages.business.portlet.ChildPagesPortlet;
import fr.paris.lutece.plugins.childpages.business.portlet.ChildPagesPortletHome;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage ChildPages Portlet
 */
@RequestScoped
@Named
public class ChildPagesPortletJspBean extends PortletJspBean
{
    private static final String MESSAGE_PORTLET_TYPE_NOT_FOUND = "childpages.message.portletTypeNotFound";
    private static final String MESSAGE_PORTLET_NOT_FOUND = "childpages.message.portletNotFound";
    private static final String MESSAGE_INVALID_TOKEN = "childpages.message.invalidToken";
    private static final String ACTION_CREATE_PORTLET = "childpages.createPortlet";
    private static final String ACTION_MODIFY_PORTLET = "childpages.modifyPortlet";
    public static final String RIGHT_MANAGE_ADMIN_SITE = "CORE_ADMIN_SITE";

    private static final String MESSAGE_PORTLET_CHILD_PAGE_INEXISTENT = "childpages.message.portlet.childpageInexistent";
    private static final String MESSAGE_PORTLET_CHILD_PAGE_PARENT_NOT_VALID = "childpages.message.portlet.childpageParentNotValid";


    private static final String PARAMETER_PARENT_ID = "parent_id";

    private static final String MARK_PARENT_ID = "page_id_parent";

    /**
     * Returns the portlet properties prefix
     *
     * @return the properties prefix
     */
    public String getPropertiesPrefix( )
    {
        return "portlet.child.pages";
    }

    /**
     * Returns the portlet creation form
     *
     * @param request the HTTP request
     * @return the creation form
     */
    public String getCreate( HttpServletRequest request )
    {
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );

        if ( StringUtils.isEmpty( strPortletTypeId ) || PortletTypeHome.findByPrimaryKey( strPortletTypeId ) == null
                || PortletTypeHome.findByPrimaryKey( strPortletTypeId ).getDoCreateUrl( ) == null )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_TYPE_NOT_FOUND, getLocale( ) );
        }

        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strIdPortletType = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_PARENT_ID, strIdPage );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_CREATE_PORTLET ) );

        HtmlTemplate template = getCreateTemplate( strIdPage, strIdPortletType, model );

        return template.getHtml( );
    }

    /**
     * Returns the portlet modification form
     *
     * @param request the HTTP request
     * @return the modification form
     */
    public String getModify( HttpServletRequest request )
    {
        ChildPagesPortlet portlet = findPortlet( request );

        if ( portlet == null )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_NOT_FOUND, getLocale( ) );
        }

        int nIdParentPage = portlet.getParentPageId( );

        HashMap<String, Object> model = new HashMap<>( );
        model.put( MARK_PARENT_ID, nIdParentPage );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_MODIFY_PORTLET ) );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    /**
     * Processes portlet creation
     *
     * @param request the HTTP request
     * @return the result management URL
     */
    public String doCreate( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_CREATE_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );

        if ( StringUtils.isEmpty( strIdPage ) || !StringUtils.isNumeric( strIdPage ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        ChildPagesPortlet portlet = new ChildPagesPortlet( );
        int nIdPage = Integer.parseInt( strIdPage );

        String strParentPage = request.getParameter( PARAMETER_PARENT_ID );

        if ( ( strParentPage == null ) || strParentPage.trim( ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdParentPage;

        try
        {
            nIdParentPage = Integer.parseInt( strParentPage );
        }
        catch( NumberFormatException e )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_CHILD_PAGE_PARENT_NOT_VALID,
                AdminMessage.TYPE_STOP );
        }

        if ( !PageHome.checkPageExist( nIdParentPage ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_CHILD_PAGE_INEXISTENT,
                AdminMessage.TYPE_STOP );
        }

        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setPageId( nIdPage );

        portlet.setParentPageId( nIdParentPage );

        ChildPagesPortletHome.getInstance( ).create( portlet );

        return getPageUrl( nIdPage );
    }

    /**
     * Processes portlet modification
     *
     * @param request the HTTP request
     * @return the result management URL
     */
    public String doModify( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_MODIFY_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        ChildPagesPortlet portlet = findPortlet( request );

        if ( portlet == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        String strParentPage = request.getParameter( PARAMETER_PARENT_ID );

        if ( ( strParentPage == null ) || strParentPage.trim( ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdPageMere;

        try
        {
            nIdPageMere = Integer.parseInt( strParentPage );
        }
        catch( NumberFormatException e )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_CHILD_PAGE_PARENT_NOT_VALID,
                AdminMessage.TYPE_STOP );
        }

        if ( !PageHome.checkPageExist( nIdPageMere ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_CHILD_PAGE_INEXISTENT,
                AdminMessage.TYPE_STOP );
        }

        portlet.setParentPageId( nIdPageMere );

        portlet.update( );

        return getPageUrl( portlet.getPageId( ) );
    }

    /**
     * Finds the portlet named by the request, without throwing on a bad identifier.
     *
     * PortletHome.findByPrimaryKey of the core dereferences the row it loaded without checking it exists, so an
     * unknown identifier raises a NullPointerException there rather than returning null.
     *
     * @param request the HTTP request
     * @return the portlet, null when the identifier is missing, malformed or unknown
     */

    /**
     * Finds the child pages portlet designated by the request
     *
     * @param request
     *            the HTTP request
     * @return the portlet, null when it is unknown or of another type
     */
    private ChildPagesPortlet findPortlet( HttpServletRequest request )
    {
        String strIdPortlet = request.getParameter( PARAMETER_PORTLET_ID );

        if ( StringUtils.isEmpty( strIdPortlet ) || !StringUtils.isNumeric( strIdPortlet ) )
        {
            return null;
        }

        Portlet portlet;

        try
        {
            portlet = PortletHome.findByPrimaryKey( Integer.parseInt( strIdPortlet ) );
        }
        catch( NullPointerException e )
        {
            AppLogService.info( "Unknown portlet {}", strIdPortlet );
            return null;
        }

        return portlet instanceof ChildPagesPortlet ? (ChildPagesPortlet) portlet : null;
    }
}
