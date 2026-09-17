/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.childpages.business.portlet;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.AdminPageJspBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class represents business objects ChildPagesPortlet
 */
public class ChildPagesPortlet extends PortletHtmlContent
{
    private static final String TEMPLATE_PORTLET = "skin/plugins/childpages/portlet/childpages_portlet.html";
    private static final String MARK_PORTLET = "portlet";
    private static final String MARK_CHILD_PAGES = "child_pages";
    private static final String MARK_SITE_PATH = "site_path";
    private static final String MARK_DEVICE_CLASS = "device_class";
    private static final String CLASS_HIDDEN_PHONE = "hidden-phone";

    private int _nParentPageId;

    /**
     * Builds a portlet with its type identifier
     */
    public ChildPagesPortlet( )
    {
        setPortletTypeId( ChildPagesPortletHome.getInstance( ).getPortletTypeId( ) );
    }

    /**
     * Sets the parent page identifier
     *
     * @param nParentPageId the parent page identifier
     */
    public void setParentPageId( int nParentPageId )
    {
        _nParentPageId = nParentPageId;
    }

    /**
     * Returns the parent page identifier
     *
     * @return the parent page identifier
     */
    public int getParentPageId( )
    {
        return _nParentPageId;
    }

    /**
     * Returns the HTML content of the portlet
     *
     * @param request the HTTP request
     * @return the rendered portlet
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_PORTLET, this );
        model.put( MARK_SITE_PATH, AppPathService.getPortalUrl( ) );
        model.put( MARK_CHILD_PAGES, getVisibleChildPages( request ) );
        model.put( MARK_DEVICE_CLASS,
                ( getDeviceDisplayFlags( ) & FLAG_DISPLAY_ON_SMALL_DEVICE ) != 0 ? "" : CLASS_HIDDEN_PHONE );

        return AppTemplateService.getTemplate( TEMPLATE_PORTLET, request != null ? request.getLocale( ) : null, model )
                .getHtml( );
    }

    /**
     * Collects the child pages visible to the current user
     *
     * @param request the HTTP request
     * @return the visible child pages, with their image URL when they have one
     */
    private List<ChildPageItem> getVisibleChildPages( HttpServletRequest request )
    {
        List<ChildPageItem> items = new ArrayList<>( );

        if ( request == null )
        {
            return items;
        }

        int nPageId = getParentPageId( ) == 0 ? getPageId( ) : getParentPageId( );
        Collection<Page> pages = PageHome.getChildPages( nPageId );
        AdminPageJspBean adminPage = new AdminPageJspBean( );

        for ( Page page : pages )
        {
            if ( !page.isVisible( request ) )
            {
                continue;
            }

            String strImageUrl = null;

            if ( page.getImageContent( ) != null && page.getImageContent( ).length >= 1 )
            {
                strImageUrl = adminPage.getResourceImagePage( page, String.valueOf( page.getId( ) ) );
            }

            items.add( new ChildPageItem( page, strImageUrl ) );
        }

        return items;
    }

    /**
     * Updates the current portlet instance
     */
    public void update( )
    {
        ChildPagesPortletHome.getInstance( ).update( this );
    }

    /**
     * Removes the current portlet instance
     */
    public void remove( )
    {
        ChildPagesPortletHome.getInstance( ).remove( this );
    }

    /**
     * A child page as the template consumes it
     */
    public static final class ChildPageItem
    {
        private final Page _page;
        private final String _strImageUrl;

        /**
         * Builds an item
         *
         * @param page the page
         * @param strImageUrl the image URL, null when the page has no image
         */
        ChildPageItem( Page page, String strImageUrl )
        {
            _page = page;
            _strImageUrl = strImageUrl;
        }

        /**
         * Returns the page identifier
         *
         * @return the page identifier
         */
        public int getId( )
        {
            return _page.getId( );
        }

        /**
         * Returns the page name
         *
         * @return the page name
         */
        public String getName( )
        {
            return _page.getName( );
        }

        /**
         * Returns the page description
         *
         * @return the page description
         */
        public String getDescription( )
        {
            return _page.getDescription( );
        }

        /**
         * Returns the image URL
         *
         * @return the image URL, null when the page has no image
         */
        public String getImageUrl( )
        {
            return _strImageUrl;
        }
    }
}
