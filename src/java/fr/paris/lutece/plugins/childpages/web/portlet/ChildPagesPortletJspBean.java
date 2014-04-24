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
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage ChildPages Portlet
 */
public class ChildPagesPortletJspBean extends PortletJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // Right
    public static final String RIGHT_MANAGE_ADMIN_SITE = "CORE_ADMIN_SITE";

    // Messages
    private static final String MESSAGE_PORTLET_CHILD_PAGE_INEXISTENT = "childpages.message.portlet.childpageInexistent";
    private static final String MESSAGE_PORTLET_CHILD_PAGE_PARENT_NOT_VALID = "childpages.message.portlet.childpageParentNotValid";

    // Templates
    private static final String TEMPLATE_HELP_PARENT_PAGE = "admin/plugins/childpages/help_parent_page.html";

    // Parameters
    private static final String PARAMETER_PARENT_ID = "parent_id";

    // Bookmarks
    private static final String MARK_PARENT_ID = "page_id_parent";

    /**
     * Returns portlet's properties prefix
     *
     * @return prefix
     */
    public String getPropertiesPrefix(  )
    {
        return "portlet.child.pages";
    }

    /**
     * Returns the Download portlet creation form
     *
     * @param request The http request
     * @return The HTML form
     */
    public String getCreate( HttpServletRequest request )
    {
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strIdPortletType = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        HashMap model = new HashMap(  );
        model.put( MARK_PARENT_ID, strIdPage );

        HtmlTemplate template = getCreateTemplate( strIdPage, strIdPortletType, model );

        return template.getHtml(  );
    }

    public String getModify( HttpServletRequest request )
    {
        String strIdPortlet = request.getParameter( PARAMETER_PORTLET_ID );
        int nIdPortlet = Integer.parseInt( strIdPortlet );
        ChildPagesPortlet portlet = (ChildPagesPortlet) PortletHome.findByPrimaryKey( nIdPortlet );

        //initialization of nParentPageId
        int nIdParentPage = portlet.getParentPageId(  );

        HashMap model = new HashMap(  );
        model.put( MARK_PARENT_ID, nIdParentPage );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml(  );
    }

    /**
     * Process portlet's creation
     *
     * @param request The Http request
     * @return The Jsp management URL of the process result
     */
    public String doCreate( HttpServletRequest request )
    {
        ChildPagesPortlet portlet = new ChildPagesPortlet(  );
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        int nIdPage = Integer.parseInt( strIdPage );

        //gets the identifier of the parent page
        String strParentPage = request.getParameter( PARAMETER_PARENT_ID );

        //Mandatory fields
        if ( strParentPage.trim(  ).equals( "" ) || ( strParentPage == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdParentPage;

        try
        {
            nIdParentPage = Integer.parseInt( strParentPage );
        }
        catch ( NumberFormatException nb )
        {
            //the format of the identifier of the page is not valid
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_CHILD_PAGE_PARENT_NOT_VALID,
                AdminMessage.TYPE_STOP );
        }

        try
        {
            PageHome.getPage( nIdParentPage );
        }
        catch ( AppException ex )
        {
            //The exception is thrown if the page doesn't exist
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_CHILD_PAGE_INEXISTENT,
                AdminMessage.TYPE_STOP );
        }

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setPageId( nIdPage );

        //gets the specific parameters
        portlet.setParentPageId( nIdParentPage );

        //Portlet creation
        ChildPagesPortletHome.getInstance(  ).create( portlet );

        //Displays the page with the new Portlet
        return getPageUrl( nIdPage );
    }

    /**
     * Process portlet's modification
     *
     * @param request The http request
     * @return Management's Url
     */
    public String doModify( HttpServletRequest request )
    {
        //recovery of the portlet
        String strIdPortlet = request.getParameter( PARAMETER_PORTLET_ID );
        int nIdPortlet = Integer.parseInt( strIdPortlet );
        ChildPagesPortlet portlet = (ChildPagesPortlet) PortletHome.findByPrimaryKey( nIdPortlet );

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        //recovery of the identifier of the parent page
        String strParentPage = request.getParameter( PARAMETER_PARENT_ID );

        if ( strParentPage.trim(  ).equals( "" ) || ( strParentPage == null ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        int nIdPageMere;

        try
        {
            nIdPageMere = Integer.parseInt( strParentPage );
        }
        catch ( NumberFormatException nb )
        {
            //the format of the identifier of the page is not valid
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_CHILD_PAGE_PARENT_NOT_VALID,
                AdminMessage.TYPE_STOP );
        }

        try
        {
            PageHome.getPage( nIdPageMere );
        }
        catch ( AppException ex )
        {
            //The exception is thrown if the page doesn't exist
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_CHILD_PAGE_INEXISTENT,
                AdminMessage.TYPE_STOP );
        }

        //gets the specific attributes of the portlet
        portlet.setParentPageId( nIdPageMere );

        //Update of the portlet
        portlet.update(  );

        //Displays the page with the updated portlet
        return getPageUrl( portlet.getPageId(  ) );
    }

    /**
     * displays the form to help the parent page input of the child page portlet
     *
     * @return the html code to help
     */
    public String getHelpParentPage( HttpServletRequest request )
    {
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_HELP_PARENT_PAGE );

        return template.getHtml(  );
    }
}
