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
package fr.paris.lutece.plugins.childpages.business.portlet;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.portal.business.style.PageTemplateHome;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

public class EventListenerTest extends LuteceTestCase
{
    private static final String PORLET_NAME = "ChildPagesPortletTest";
    private static final int TEMPLATE_ONE_COLUMN = 2;
    private static final String MARKER_PORTLET_START = "portlet-background";
    private static final String MARKER_PORTLET_END = "</div>\n</div>";

    private int _nHostPageId;

    @Inject
    private IPageService _pageService;

    /**
     * Creates the test portlet
     *
     * @throws Exception if an error occurs
     */
    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        Page hostPage = new Page( );
        hostPage.setParentPageId( PortalService.getRootPageId( ) );
        hostPage.setPageTemplateId( TEMPLATE_ONE_COLUMN );
        hostPage.setName( PORLET_NAME + "Host" );
        hostPage.setDescription( PORLET_NAME + "Host" );
        _pageService.createPage( hostPage );
        _nHostPageId = hostPage.getId( );

        ChildPagesPortlet portlet = new ChildPagesPortlet( );
        portlet.setParentPageId( PortalService.getRootPageId( ) );
        portlet.setPageId( _nHostPageId );
        portlet.setStyleId( resolveStyleId( ) );
        portlet.setColumn( 1 );
        portlet.setOrder( 1 );
        portlet.setName( PORLET_NAME );
        portlet.setStatus( Portlet.STATUS_PUBLISHED );
        ChildPagesPortletHome.getInstance( ).create( portlet );
    }

    /**
     * Removes the test portlet
     *
     * @throws Exception if an error occurs
     */
    @AfterEach
    protected void tearDown( ) throws Exception
    {
        ChildPagesPortlet portlet = findTestPortlet( );
        if ( portlet != null )
        {
            ChildPagesPortletHome.getInstance( ).remove( portlet );
        }
        if ( _nHostPageId != 0 )
        {
            _pageService.removePage( _nHostPageId );
            _nHostPageId = 0;
        }
        LocalVariables.remove( );
        super.tearDown( );
    }

    /**
     * Tests that page modifications are reflected in the portlet
     *
     * @throws SiteMessageException if a site message is raised
     */
    @Test
    public void testProcessEvent( ) throws SiteMessageException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        MockHttpServletResponse response = new MockHttpServletResponse( );
        LocalVariables.setLocal( null, request, response );
        String randomPageName = "page" + new SecureRandom( ).nextLong( );
        String content = portletContent( request );
        assertFalse( content.contains( randomPageName ),
                "page should not contain not yet created page with name " + randomPageName );
        assertFalse( content.contains( randomPageName + "_desc" ),
                "page should not contain not yet created page with description " + randomPageName + "_desc" );
        Page page = new Page( );
        page.setParentPageId( PortalService.getRootPageId( ) );
        page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        page.setName( randomPageName );
        page.setDescription( randomPageName + "_desc" );
        _pageService.createPage( page );
        content = portletContent( request );
        assertTrue( content.contains( randomPageName ),
                "page should contain page with name " + randomPageName );
        assertTrue( content.contains( randomPageName + "_desc" ),
                "page should contain page with decription " + randomPageName + "_desc" );
        randomPageName = randomPageName + "_mod";
        page.setName( randomPageName );
        page.setDescription( randomPageName + "_desc" );
        _pageService.updatePage( page );
        content = portletContent( request );
        assertTrue( content.contains( randomPageName ),
                "page should contain page with the modified name " + randomPageName );
        assertTrue( content.contains( randomPageName + "_desc" ),
                "page should contain page with decription " + randomPageName + "_desc" );
        _pageService.removePage( page.getId( ) );
        content = portletContent( request );
        assertFalse( content.contains( randomPageName ),
                "page should not contain page with name " + randomPageName + " anymore" );
        assertFalse( content.contains( randomPageName + "_desc" ),
                "page should not contain not yet created page with description " + randomPageName + "_desc anymore" );
    }

    /**
     * Tests that a page parent change is reflected in the portlet
     *
     * @throws SiteMessageException if a site message is raised
     */
    @Test
    public void testProcessEventUpdatePageParent( ) throws SiteMessageException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        MockHttpServletResponse response = new MockHttpServletResponse( );
        LocalVariables.setLocal( null, request, response );
        String randomPageName = "page" + new SecureRandom( ).nextLong( );
        String content = portletContent( request );
        assertFalse( content.contains( randomPageName ),
                "page should not contain not yet created page with name " + randomPageName );
        assertFalse( content.contains( randomPageName + "_desc" ),
                "page should not contain not yet created page with description " + randomPageName + "_desc" );
        Page page = new Page( );
        page.setParentPageId( PortalService.getRootPageId( ) );
        page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        page.setName( randomPageName );
        page.setDescription( randomPageName + "_desc" );
        _pageService.createPage( page );
        content = portletContent( request );
        assertTrue( content.contains( randomPageName ),
                "page should contain page with name " + randomPageName );
        assertTrue( content.contains( randomPageName + "_desc" ),
                "page should contain page with decription " + randomPageName + "_desc" );
        Page siblingPage = new Page( );
        siblingPage.setParentPageId( PortalService.getRootPageId( ) );
        siblingPage.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        siblingPage.setName( randomPageName + "_sibling" );
        siblingPage.setDescription( randomPageName + "_sibling_desc" );
        _pageService.createPage( siblingPage );
        content = portletContent( request );
        assertTrue( content.contains( randomPageName + "_sibling" ),
                "page should contain page with name " + randomPageName + "_sibling" );
        assertTrue( content.contains( randomPageName + "_sibling_desc" ),
                "page should contain page with decription " + randomPageName + "_sibling_desc" );
        siblingPage.setParentPageId( page.getId( ) );
        _pageService.updatePage( siblingPage );
        content = portletContent( request );
        assertFalse( content.contains( randomPageName + "_sibling" ),
                "page should not contain page with name " + randomPageName + "_sibling anymore" );
        assertFalse( content.contains( randomPageName + "_sibling_desc" ),
                "page should not contain not yet created page with description " + randomPageName + "_sibling_desc anymore" );
        _pageService.removePage( siblingPage.getId( ) );
        _pageService.removePage( page.getId( ) );
    }

    /**
     * Tests that no event loop occurs
     */
    @Timeout( value = 10, unit = TimeUnit.SECONDS )
    @Test
    public void testProcessEventLoopPrevention( )
    {
        String randomPageName = "page" + new SecureRandom( ).nextLong( );
        Page page = new Page( );
        page.setParentPageId( PortalService.getRootPageId( ) );
        page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        page.setName( randomPageName );
        page.setDescription( randomPageName + "_desc" );
        _pageService.createPage( page );
        ChildPagesPortlet portlet = new ChildPagesPortlet( );
        portlet.setParentPageId( page.getId( ) );
        portlet.setPageId( page.getId( ) );
        portlet.setStyleId( resolveStyleId( ) );
        portlet.setColumn( 1 );
        portlet.setOrder( 1 );
        portlet.setName( PORLET_NAME );
        portlet.setStatus( Portlet.STATUS_PUBLISHED );
        ChildPagesPortletHome.getInstance( ).create( portlet );
        Page page2 = new Page( );
        page2.setParentPageId( page.getId( ) );
        page2.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        page2.setName( randomPageName + "_child" );
        page2.setDescription( randomPageName + "_child_desc" );
        _pageService.createPage( page2 );
        ChildPagesPortlet portlet2 = new ChildPagesPortlet( );
        portlet2.setParentPageId( PortalService.getRootPageId( ) );
        portlet2.setPageId( page2.getId( ) );
        portlet2.setStyleId( resolveStyleId( ) );
        portlet2.setColumn( 1 );
        portlet2.setOrder( 1 );
        portlet2.setName( PORLET_NAME );
        portlet2.setStatus( Portlet.STATUS_PUBLISHED );
        ChildPagesPortletHome.getInstance( ).create( portlet2 );
        _pageService.removePage( page2.getId( ) );
        _pageService.removePage( page.getId( ) );
    }

    /**
     * Renders the host page and keeps only what the portlet produced
     *
     * @param request the HTTP request
     * @return the portlet markup, empty when the portlet rendered nothing
     * @throws SiteMessageException if a site message is raised
     */
    private String portletContent( MockHttpServletRequest request ) throws SiteMessageException
    {
        String content = _pageService.getPage( String.valueOf( _nHostPageId ), 0, request );
        int nStart = content.indexOf( MARKER_PORTLET_START );

        if ( nStart < 0 )
        {
            return "";
        }

        int nEnd = content.indexOf( MARKER_PORTLET_END, nStart );

        return nEnd < 0 ? content.substring( nStart ) : content.substring( nStart, nEnd );
    }

    /**
     * Resolves the first style declared for the child pages portlet type
     *
     * @return the style identifier, 0 when no style is available
     */
    private int resolveStyleId( )
    {
        ReferenceList styles = PortletHome.getStylesList(
                ChildPagesPortletHome.getInstance( ).getPortletTypeId( ) );
        return styles.isEmpty( ) ? 0 : Integer.parseInt( styles.get( 0 ).getCode( ) );
    }

    /**
     * Finds the test portlet on the root page
     *
     * @return the test portlet, null when absent
     */
    private ChildPagesPortlet findTestPortlet( )
    {
        Page rootPage = PageHome.findByPrimaryKey( _nHostPageId );
        List<Portlet> portlets = rootPage.getPortlets( );
        for ( Portlet aPortlet : portlets )
        {
            if ( aPortlet.getPortletTypeId( ).equals(
                    ChildPagesPortletHome.getInstance( ).getPortletTypeId( ) )
                    && aPortlet.getName( ).equals( PORLET_NAME ) )
            {
                return (ChildPagesPortlet) aPortlet;
            }
        }
        return null;
    }

}
