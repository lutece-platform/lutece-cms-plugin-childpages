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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletTemplate;
import fr.paris.lutece.portal.business.portlet.PortletTemplateHome;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

/**
 * Renders a child pages portlet with every shipped FreeMarker template
 */
public class ChildPagesPortletRenderingTest extends LuteceTestCase
{
    private static final String PORTLET_NAME = "ChildPagesPortletRenderingTest";
    private static final int TEMPLATE_ONE_COLUMN = 2;
    private static final String MARKER_PORTLET = "portlet-childpages";
    private static final int UNKNOWN_TEMPLATE_ID = 99999;

    private int _nParentPageId;
    private int _nChildPageId;
    private String _strChildPageName;
    private ChildPagesPortlet _portlet;

    @Inject
    private IPageService _pageService;

    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        Page parentPage = new Page( );
        parentPage.setParentPageId( PortalService.getRootPageId( ) );
        parentPage.setPageTemplateId( TEMPLATE_ONE_COLUMN );
        parentPage.setName( PORTLET_NAME + "Parent" );
        parentPage.setDescription( PORTLET_NAME + "Parent" );
        _pageService.createPage( parentPage );
        _nParentPageId = parentPage.getId( );

        _strChildPageName = "page" + new SecureRandom( ).nextLong( );
        Page childPage = new Page( );
        childPage.setParentPageId( _nParentPageId );
        childPage.setPageTemplateId( TEMPLATE_ONE_COLUMN );
        childPage.setName( _strChildPageName );
        childPage.setDescription( _strChildPageName + "_desc" );
        _pageService.createPage( childPage );
        _nChildPageId = childPage.getId( );

        _portlet = new ChildPagesPortlet( );
        _portlet.setParentPageId( _nParentPageId );
        _portlet.setPageId( _nParentPageId );
        _portlet.setStyleId( 0 );
        _portlet.setColumn( 1 );
        _portlet.setOrder( 1 );
        _portlet.setName( PORTLET_NAME );
        _portlet.setStatus( Portlet.STATUS_PUBLISHED );
        _portlet.setDisplayPortletTitle( 0 );
        _portlet.setDeviceDisplayFlags( Portlet.FLAG_DISPLAY_ON_NORMAL_DEVICE | Portlet.FLAG_DISPLAY_ON_LARGE_DEVICE | Portlet.FLAG_DISPLAY_ON_XLARGE_DEVICE );
        ChildPagesPortletHome.getInstance( ).create( _portlet );
    }

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        if ( _portlet != null )
        {
            ChildPagesPortletHome.getInstance( ).remove( _portlet );
        }
        if ( _nChildPageId != 0 )
        {
            _pageService.removePage( _nChildPageId );
        }
        if ( _nParentPageId != 0 )
        {
            _pageService.removePage( _nParentPageId );
        }
        LocalVariables.remove( );
        super.tearDown( );
    }

    /**
     * Every shipped template renders the portlet title, the child page and the device display classes
     */
    @Test
    public void testRenderEveryShippedTemplate( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        List<PortletTemplate> listTemplates = PortletTemplateHome.findByPortletType( ChildPagesPortletHome.getInstance( ).getPortletTypeId( ) );
        assertEquals( 3, listTemplates.size( ), "the three shipped templates should be registered in the core for the child pages portlet type" );

        for ( PortletTemplate template : listTemplates )
        {
            _portlet.setIdTemplate( template.getId( ) );
            String strContent = _portlet.getHtmlContent( request );

            assertTrue( strContent.contains( MARKER_PORTLET ), "template " + template.getTemplatePath( ) + " should render the portlet wrapper" );
            assertTrue( strContent.contains( PORTLET_NAME ), "template " + template.getTemplatePath( ) + " should render the portlet title" );
            assertTrue( strContent.contains( _strChildPageName ), "template " + template.getTemplatePath( ) + " should render the child page name" );
            assertTrue( strContent.contains( _strChildPageName + "_desc" ), "template " + template.getTemplatePath( ) + " should render the child page description" );
            assertTrue( strContent.contains( "page_id=" + _nChildPageId ), "template " + template.getTemplatePath( ) + " should link to the child page" );
            assertTrue( strContent.contains( "d-none d-md-block" ), "template " + template.getTemplatePath( ) + " should hide the portlet on small devices" );
        }
    }

    /**
     * An unknown template falls back to the default one and a hidden title is not rendered
     */
    @Test
    public void testFallbackToDefaultTemplate( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        _portlet.setIdTemplate( UNKNOWN_TEMPLATE_ID );
        _portlet.setDisplayPortletTitle( 1 );
        String strContent = _portlet.getHtmlContent( request );

        assertTrue( strContent.contains( MARKER_PORTLET ), "the default template should render the portlet wrapper" );
        assertTrue( strContent.contains( _strChildPageName ), "the default template should render the child page name" );
        assertFalse( strContent.contains( PORTLET_NAME ), "a hidden portlet title should not be rendered" );
    }
}
