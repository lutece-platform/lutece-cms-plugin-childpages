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

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;


/**
 * This class provides Data Access methods for ChildPagesPortlet objects
 */
@ApplicationScoped
public final class ChildPagesPortletDAO implements IChildPagesPortletDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO childpages_portlet ( id_portlet, id_child_page ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM childpages_portlet WHERE id_portlet = ?";
    private static final String SQL_QUERY_SELECT = "SELECT id_portlet, id_child_page FROM childpages_portlet WHERE id_portlet = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE childpages_portlet SET id_child_page = ? WHERE id_portlet = ?";
    private static final String SQL_QUERY_SELECT_CHILDPAGE_LIST = "SELECT id_page, name FROM core_page WHERE id_page = ?";
    private static final String SQL_QUERY_SELECT_PAGE_LIST = "SELECT id_page, name FROM core_page";
    private static final String SQL_QUERY_SELECT_BY_PARENTPAGEID = "SELECT id_portlet, id_child_page FROM childpages_portlet WHERE id_child_page = ?";

    /**
     * Inserts a new record in the table
     *
     * @param portlet the portlet to insert
     */
    public void insert( Portlet portlet )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            ChildPagesPortlet p = (ChildPagesPortlet) portlet;
            daoUtil.setInt( 1, p.getId( ) );
            daoUtil.setInt( 2, p.getParentPageId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Deletes a record from the table
     *
     * @param nPortletId the portlet identifier
     */
    public void delete( int nPortletId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Loads a portlet by identifier
     *
     * @param nPortletId the portlet identifier
     * @return the loaded portlet
     */
    public Portlet load( int nPortletId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeQuery( );

            ChildPagesPortlet portlet = new ChildPagesPortlet( );

            if ( daoUtil.next( ) )
            {
                portlet.setId( daoUtil.getInt( 1 ) );
                portlet.setParentPageId( daoUtil.getInt( 2 ) );
            }

            return portlet;
        }
    }

    /**
     * Updates a record in the table
     *
     * @param portlet the portlet to update
     */
    public void store( Portlet portlet )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            ChildPagesPortlet p = (ChildPagesPortlet) portlet;
            daoUtil.setInt( 1, p.getParentPageId( ) );
            daoUtil.setInt( 2, p.getId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Loads the child pages of a page
     *
     * @param nPageId the page identifier
     * @return the child pages list
     */
    public ReferenceList selectChildPagesList( int nPageId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_CHILDPAGE_LIST ) )
        {
            daoUtil.setInt( 1, nPageId );
            daoUtil.executeQuery( );

            ReferenceList list = new ReferenceList( );

            while ( daoUtil.next( ) )
            {
                list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return list;
        }
    }

    /**
     * Loads all the pages of the database
     *
     * @return the pages list
     */
    public ReferenceList selectPagesList( )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PAGE_LIST ) )
        {
            daoUtil.executeQuery( );

            ReferenceList list = new ReferenceList( );

            while ( daoUtil.next( ) )
            {
                list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

            return list;
        }
    }

    /**
     * Loads the portlets bound to a parent page
     *
     * @param parentPageId the parent page identifier
     * @return the portlets list
     */
    @Override
    public List<ChildPagesPortlet> getChildPagesPortlets( int parentPageId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PARENTPAGEID ) )
        {
            daoUtil.setInt( 1, parentPageId );
            daoUtil.executeQuery( );

            List<ChildPagesPortlet> res = new ArrayList<>( );

            while ( daoUtil.next( ) )
            {
                ChildPagesPortlet portlet = new ChildPagesPortlet( );
                portlet.setId( daoUtil.getInt( 1 ) );
                portlet.setParentPageId( daoUtil.getInt( 2 ) );
                res.add( portlet );
            }

            return res;
        }
    }
}
