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

import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.style.PageTemplateHome;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.MokeHttpServletRequest;

public class EventListenerTest extends LuteceTestCase
{
    // FIXME : remove when LUTECE-1838 is resolved
    private static final class MokeHttpServletResponse implements
            HttpServletResponse
    {
        @Override
        public void setLocale( Locale loc )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setContentType( String type )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setContentLength( int len )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setCharacterEncoding( String charset )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setBufferSize( int size )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void resetBuffer( )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void reset( )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean isCommitted( )
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public PrintWriter getWriter( ) throws IOException
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ServletOutputStream getOutputStream( ) throws IOException
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Locale getLocale( )
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getContentType( )
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getCharacterEncoding( )
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getBufferSize( )
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void flushBuffer( ) throws IOException
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setStatus( int sc, String sm )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setStatus( int sc )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setIntHeader( String name, int value )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setHeader( String name, String value )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void setDateHeader( String name, long date )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void sendRedirect( String location ) throws IOException
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void sendError( int sc, String msg ) throws IOException
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void sendError( int sc ) throws IOException
        {
            // TODO Auto-generated method stub

        }

        @Override
        public String encodeUrl( String url )
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String encodeURL( String url )
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String encodeRedirectUrl( String url )
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String encodeRedirectURL( String url )
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean containsHeader( String name )
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void addIntHeader( String name, int value )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void addHeader( String name, String value )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void addDateHeader( String name, long date )
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void addCookie( Cookie cookie )
        {
            // TODO Auto-generated method stub

        }
    }

    private static final String PORLET_NAME = "ChildPagesPortletTest";

    @Before
    public void setUp( ) throws Exception
    {
        super.setUp( );
        ChildPagesPortlet portlet = new ChildPagesPortlet( );
        portlet.setParentPageId( 1 );
        portlet.setPageId( 1 );
        portlet.setStyleId( Integer
                .parseInt( PortletHome
                        .getStylesList(
                                ChildPagesPortletHome.getInstance( )
                                        .getPortletTypeId( ) ).get( 0 )
                        .getCode( ) ) );
        portlet.setColumn( 1 );
        portlet.setOrder( 1 );
        portlet.setName( PORLET_NAME );
        portlet.setStatus( Portlet.STATUS_PUBLISHED );
        ChildPagesPortletHome.getInstance( ).create( portlet );
    }

    @After
    public void tearDown( ) throws Exception
    {
        ChildPagesPortlet portlet = findTestPortlet( );
        if ( portlet != null )
        {
            ChildPagesPortletHome.getInstance( ).remove( portlet );
        }
        super.tearDown( );
    }

    /**
     * test that page modification are reflected in the portlet
     * 
     * @throws SiteMessageException
     */
    @Test
    public void testProcessEvent( ) throws SiteMessageException
    {
        // FIXME : rework when LUTECE-1837 is resolved
        MokeHttpServletRequest request = new MokeHttpServletRequest( )
        {

            @Override
            public Object getAttribute( String string )
            {
                return null;
            }

        };
        HttpServletResponse response = new MokeHttpServletResponse( );
        LocalVariables.setLocal( null, request, response );
        // determine a random page name
        String randomPageName = "page" + new SecureRandom( ).nextLong( );
        // get the page content
        IPageService pageService = ( IPageService ) SpringContextService
                .getBean( "pageService" );
        String content = pageService.getPage( "1", 0, request );
        assertFalse( "page should not contain not yet created page with name "
                + randomPageName, content.contains( randomPageName ) );
        assertFalse(
                "page should not contain not yet created page with description "
                        + randomPageName + "_desc",
                content.contains( randomPageName + "_desc" ) );
        // create the page
        Page page = new Page( );
        page.setParentPageId( PortalService.getRootPageId( ) );
        page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        page.setName( randomPageName );
        page.setDescription( randomPageName + "_desc" );
        pageService.createPage( page );
        // get the page content
        content = pageService.getPage( "1", 0, request );
        assertTrue( "page should contain page with name " + randomPageName,
                content.contains( randomPageName ) );
        assertTrue( "page should contain page with decription "
                + randomPageName + "_desc",
                content.contains( randomPageName + "_desc" ) );
        // change the page name
        randomPageName = randomPageName + "_mod";
        page.setName( randomPageName );
        page.setDescription( randomPageName + "_desc" );
        pageService.updatePage( page );
        // get the page content
        content = pageService.getPage( "1", 0, request );
        assertTrue( "page should contain page with the modified name "
                + randomPageName, content.contains( randomPageName ) );
        assertTrue( "page should contain page with decription "
                + randomPageName + "_desc",
                content.contains( randomPageName + "_desc" ) );
        // remove the page
        pageService.removePage( page.getId( ) );
        // get the page content
        content = pageService.getPage( "1", 0, request );
        assertFalse( "page should not contain page with name " + randomPageName
                + " anymore", content.contains( randomPageName ) );
        assertFalse(
                "page should not contain not yet created page with description "
                        + randomPageName + "_desc anymore",
                content.contains( randomPageName + "_desc" ) );
    }

    /**
     * test that changing a page parent id is reflected in the portlet
     * 
     * @see <a
     *      href="https://dev.lutece.paris.fr/jira/browse/CHILDPAGES-7">https://dev.lutece.paris.fr/jira/browse/CHILDPAGES-7</a>
     * @throws SiteMessageException
     */
    @Test
    public void testProcessEventUpdatePageParent( ) throws SiteMessageException
    {
        // FIXME : rework when LUTECE-1837 is resolved
        MokeHttpServletRequest request = new MokeHttpServletRequest( )
        {

            @Override
            public Object getAttribute( String string )
            {
                return null;
            }

        };
        HttpServletResponse response = new MokeHttpServletResponse( );
        LocalVariables.setLocal( null, request, response );
        // determine a random page name
        String randomPageName = "page" + new SecureRandom( ).nextLong( );
        // get the page content
        IPageService pageService = ( IPageService ) SpringContextService
                .getBean( "pageService" );
        String content = pageService.getPage( "1", 0, request );
        assertFalse( "page should not contain not yet created page with name "
                + randomPageName, content.contains( randomPageName ) );
        assertFalse(
                "page should not contain not yet created page with description "
                        + randomPageName + "_desc",
                content.contains( randomPageName + "_desc" ) );
        // create the page
        Page page = new Page( );
        page.setParentPageId( PortalService.getRootPageId( ) );
        page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        page.setName( randomPageName );
        page.setDescription( randomPageName + "_desc" );
        pageService.createPage( page );
        // get the page content
        content = pageService.getPage( "1", 0, request );
        assertTrue( "page should contain page with name " + randomPageName,
                content.contains( randomPageName ) );
        assertTrue( "page should contain page with decription "
                + randomPageName + "_desc",
                content.contains( randomPageName + "_desc" ) );
        // create a sibling page
        Page siblingPage = new Page( );
        siblingPage.setParentPageId( PortalService.getRootPageId( ) );
        siblingPage.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        siblingPage.setName( randomPageName + "_sibling" );
        siblingPage.setDescription( randomPageName + "_sibling_desc" );
        pageService.createPage( siblingPage );
        // get the page content
        content = pageService.getPage( "1", 0, request );
        assertTrue( "page should contain page with name " + randomPageName
                + "_sibling", content.contains( randomPageName + "_sibling" ) );
        assertTrue( "page should contain page with decription "
                + randomPageName + "_sibling_desc",
                content.contains( randomPageName + "_sibling_desc" ) );
        // move the page
        siblingPage.setParentPageId( page.getId( ) );
        pageService.updatePage( siblingPage );
        // get the page content
        content = pageService.getPage( "1", 0, request );
        assertFalse( "page should not contain page with name " + randomPageName
                + "_sibling anymore",
                content.contains( randomPageName + "_sibling" ) );
        assertFalse(
                "page should not contain not yet created page with description "
                        + randomPageName + "_sibling_desc anymore",
                content.contains( randomPageName + "_sibling_desc" ) );
        // cleanup
        pageService.removePage( siblingPage.getId( ) );
        pageService.removePage( page.getId( ) );
    }

    /**
     * test that there is no event loop
     */
    @Test( timeout = 1000 )
    public void testProcessEventLoopPrevention( )
    {
        // determine a random page name
        String randomPageName = "page" + new SecureRandom( ).nextLong( );
        // get the page content
        IPageService pageService = ( IPageService ) SpringContextService
                .getBean( "pageService" );
        // create the page
        Page page = new Page( );
        page.setParentPageId( PortalService.getRootPageId( ) );
        page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( )
                .get( 0 ).getId( ) );
        page.setName( randomPageName );
        page.setDescription( randomPageName + "_desc" );
        pageService.createPage( page );
        ChildPagesPortlet portlet = new ChildPagesPortlet( );
        portlet.setParentPageId( page.getId( ) );
        portlet.setPageId( page.getId( ) );
        portlet.setStyleId( Integer
                .parseInt( PortletHome
                        .getStylesList(
                                ChildPagesPortletHome.getInstance( )
                                        .getPortletTypeId( ) ).get( 0 )
                        .getCode( ) ) );
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
        pageService.createPage( page2 );
        ChildPagesPortlet portlet2 = new ChildPagesPortlet( );
        portlet2.setParentPageId( PortalService.getRootPageId( ) );
        portlet2.setPageId( page2.getId( ) );
        portlet2.setStyleId( Integer
                .parseInt( PortletHome
                        .getStylesList(
                                ChildPagesPortletHome.getInstance( )
                                        .getPortletTypeId( ) ).get( 0 )
                        .getCode( ) ) );
        portlet2.setColumn( 1 );
        portlet2.setOrder( 1 );
        portlet2.setName( PORLET_NAME );
        portlet2.setStatus( Portlet.STATUS_PUBLISHED );
        ChildPagesPortletHome.getInstance( ).create( portlet2 );
        pageService.removePage( page2.getId( ) );
        pageService.removePage( page.getId( ) );
    }

    private ChildPagesPortlet findTestPortlet( )
    {
        Page rootPage = PageHome.findByPrimaryKey( 1 );
        List<Portlet> portlets = rootPage.getPortlets( );
        for ( Portlet aPortlet : portlets )
        {
            if ( aPortlet.getPortletTypeId( ).equals(
                    ChildPagesPortletHome.getInstance( ).getPortletTypeId( ) )
                    && aPortlet.getName( ).equals( PORLET_NAME ) )
            {
                return ( ChildPagesPortlet ) aPortlet;
            }
        }
        return null;
    }

}
