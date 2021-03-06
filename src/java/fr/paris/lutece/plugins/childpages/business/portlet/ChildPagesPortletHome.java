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

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;


/**
 * This class provides instances management methods for ChildPagesPortlet objects
 */
public class ChildPagesPortletHome extends PortletHome
{
    // Static variable pointed at the DAO instance
    private static IChildPagesPortletDAO _dao = (IChildPagesPortletDAO) SpringContextService.getPluginBean( "childpages",
            "childPagesPortletDAO" );

    /** This class implements the Singleton design pattern. */
    private static ChildPagesPortletHome _singleton = null;

    /**
     * Constructor
     */
    public ChildPagesPortletHome(  )
    {
        if ( _singleton == null )
        {
            _singleton = this;
        }
    }

    /**
     * Returns the identifier of the child pages portlet type
     *
     * @return the portlet type identifier
     */
    public String getPortletTypeId(  )
    {
        String strCurrentClassName = this.getClass(  ).getName(  );
        String strPortletTypeId = PortletTypeHome.getPortletTypeId( strCurrentClassName );

        return strPortletTypeId;
    }

    /**
     * Returns the instance of ChildPagesPortletHome
     *
     * @return the ChildPagesPortletHome instance
     */
    public static PortletHome getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new ChildPagesPortletHome(  );
        }

        return _singleton;
    }

    /**
     * Returns the instance of the ChildPagesPortletDAO singleton
     *
     * @return the instance of the ChildPagesPortletDAO
     */
    public IPortletInterfaceDAO getDAO(  )
    {
        return _dao;
    }

    /**
     * Returns the portlet parent page identifier of the portlet whose identifier is specified in parameter
     *
     * @param nPortletId the identifier of the portlet
     * @return the identifier of the parent page portlet
     */
    public static int getParentPageId( int nPortletId )
    {
        ChildPagesPortlet portlet = (ChildPagesPortlet) _dao.load( nPortletId );

        return portlet.getParentPageId(  );
    }

    /**
     * Returns the list of all the pages of the database
     *
     * @return the list in form of a ReferenceList object
     */
    public static ReferenceList getPagesList(  )
    {
        return _dao.selectPagesList(  );
    }

    /**
     * Returns the list of the child pages of a page whose identifier is specified in parameter
     *
     * @param nPageId the identifier of the page
     * @return the list in form of a ReferenceList object
     */
    public static ReferenceList getChildPagesList( int nPageId )
    {
        return _dao.selectChildPagesList( nPageId );
    }
    
    /**
     * Returns the list of ChildPages portlets whose parent page id is specified
     * 
     * @param parentPageId the parent page id
     * @return list of ChildPages portlets whose parent page id is parentPageId
     */
    public static List<ChildPagesPortlet> getChildPagesPortlets( int parentPageId )
    {
        List<ChildPagesPortlet> portlets = _dao.getChildPagesPortlets( parentPageId );
        List<ChildPagesPortlet> res = new ArrayList<ChildPagesPortlet>( portlets.size( ) );
        for ( ChildPagesPortlet aPortlet : portlets)
        {
            res.add( ( ChildPagesPortlet ) findByPrimaryKey( aPortlet.getId( ) ) );
        }
        return res;
    }
}
