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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.page.PageEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * Invalidates child pages portlets on page events
 */
@ApplicationScoped
public class EventListener
{
    private final ThreadLocal<Boolean> secondaryEvent;

    /**
     * Builds the listener
     */
    public EventListener( )
    {
        secondaryEvent = ThreadLocal.withInitial( ( ) -> Boolean.FALSE );
    }

    /**
     * Invalidates child pages portlets on page change
     *
     * @param event the page event
     */
    public void processPageEvent( @Observes PageEvent event )
    {
        if ( secondaryEvent.get( ) )
        {
            return;
        }
        secondaryEvent.set( Boolean.TRUE );
        try
        {
            int parentPageId = event.getPage( ).getParentPageId( );
            if ( parentPageId == 0 )
            {
                return;
            }
            List<Integer> parentPageIds = new ArrayList<>( 2 );
            parentPageIds.add( parentPageId );
            if ( parentPageId != event.getPage( ).getOrigParentPageId( ) )
            {
                parentPageIds.add( event.getPage( ).getOrigParentPageId( ) );
            }
            for ( Integer pageId : parentPageIds )
            {
                Page parentPage = PageHome.findByPrimaryKey( pageId );
                List<Portlet> portlets = parentPage.getPortlets( );
                String childPagesPortletId = ChildPagesPortletHome.getInstance( ).getPortletTypeId( );
                for ( Portlet aPortlet : portlets )
                {
                    if ( aPortlet.getPortletTypeId( ).equals( childPagesPortletId ) )
                    {
                        ChildPagesPortlet cpPortlet = (ChildPagesPortlet) aPortlet;
                        if ( cpPortlet.getParentPageId( ) == 0 )
                        {
                            PortletHome.invalidate( aPortlet );
                        }
                    }
                }
                List<ChildPagesPortlet> childPagesportlets = ChildPagesPortletHome.getChildPagesPortlets( pageId );
                for ( ChildPagesPortlet aPortlet : childPagesportlets )
                {
                    PortletHome.invalidate( aPortlet );
                }
            }
        }
        finally
        {
            secondaryEvent.remove( );
        }
    }
}
