/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.ReferenceList;


/**
 *
 * @author nguyenme
 */
public interface IChildPagesPortletDAO extends IPortletInterfaceDAO
{
    /**
     * Deletes a record from the table
     *
     *
     * @param nPortletId Identifier portlet
     */
    public abstract void delete( int nPortletId );

    /**
     * Insert a new record in the table childpages_portlet
     *
     *
     * @param portlet the instance of the Portlet object to insert
     */
    public abstract void insert( Portlet portlet );

    /**
     * Loads the data of a ChildPagesPortlet whose identifier is specified in parameter from the table
     *
     *
     * @param nPortletId The ChildPagesPortlet identifier
     * @return the ChildPagesPortlet object
     */
    public abstract Portlet load( int nPortletId );

    /**
     * Load the list of the child pages of a page whose identifier is specified in parameter
     *
     *
     * @param nPageId the identifier of the page
     * @return the list in form of a ReferenceList object
     */
    public abstract ReferenceList selectChildPagesList( int nPageId );

    /**
     * Load the list of all the pages of the database
     *
     *
     * @return the list in form of a ReferenceList object
     */
    public abstract ReferenceList selectPagesList(  );

    /**
     * Updates a record in the table with the Portlet instance specified in parameter
     *
     * @param portlet the instance of Portlet class to be updated
     */
    public abstract void store( Portlet portlet );
}
