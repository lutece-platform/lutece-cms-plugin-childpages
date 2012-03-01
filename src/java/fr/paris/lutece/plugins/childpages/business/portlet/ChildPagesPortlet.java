/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.web.admin.AdminPageJspBean;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents business objects ChildPagesPortlet
 */
public class ChildPagesPortlet extends Portlet
{
    /////////////////////////////////////////////////////////////////////////////////
    // Xml Tags
    public static final String TAG_CHILD_PAGE = "child-page";
    public static final String TAG_CHILD_PAGE_ID = "child-page-id";
    public static final String TAG_CHILD_PAGE_NAME = "child-page-name";
    public static final String TAG_CHILD_PAGE_DESCRIPTION = "child-page-description";
    public static final String TAG_CHILD_PAGE_IMAGE = "child-page-image";
    public static final String TAG_CHILD_PAGES_PORTLET_LIST = "child-pages-portlet";

    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    private int _nParentPageId;

    /**
     * Sets the identifier of the portlet type to the value specified in the ChildPagesPortletHome class
     */
    public ChildPagesPortlet(  )
    {
        setPortletTypeId( ChildPagesPortletHome.getInstance(  ).getPortletTypeId(  ) );
    }

    /**
     * Sets the parent page identifier of the portlet to the value specified in parameter
     *
     * @param nParentPageId new parent page identifier
     */
    public void setParentPageId( int nParentPageId )
    {
        _nParentPageId = nParentPageId;
    }

    /**
     * Returns the identifier of the parent page of the portlet
     *
     * @return the parent page identifier
     */
    public int getParentPageId(  )
    {
        return _nParentPageId;
    }

    /**
     * Returns the Xml code of the Child pages portlet without XML heading
     *
     * @param request The HTTP Servlet Request
     * @return the Xml code of the child pages portlet content
     */
    public String getXml( HttpServletRequest request )
    {
        StringBuffer strXml = new StringBuffer(  );
        XmlUtil.beginElement( strXml, TAG_CHILD_PAGES_PORTLET_LIST );

        Iterator i = PageHome.getChildPages( getPageId(  ) ).iterator(  );

        if ( getParentPageId(  ) != 0 )
        {
            i = PageHome.getChildPages( getParentPageId(  ) ).iterator(  );
        }

        while ( i.hasNext(  ) )
        {
            Page page = (Page) i.next(  );

            if ( request != null )
            {
	            if ( page.isVisible( request ) )
	            {
	                XmlUtil.beginElement( strXml, TAG_CHILD_PAGE );
	                XmlUtil.addElement( strXml, TAG_CHILD_PAGE_ID, page.getId(  ) );
	                XmlUtil.addElement( strXml, TAG_CHILD_PAGE_NAME, page.getName(  ) );
	                XmlUtil.addElement( strXml, TAG_CHILD_PAGE_DESCRIPTION, page.getDescription(  ) );
	
	                AdminPageJspBean adminPage = new AdminPageJspBean(  );
	
	                if ( page.getImageContent(  ) != null )
	                {
	                    int nImageLength = page.getImageContent(  ).length;
	
	                    if ( nImageLength >= 1 )
	                    {
	                        String strPageId = new Integer( page.getId(  ) ).toString(  );
	                        XmlUtil.addElement( strXml, TAG_CHILD_PAGE_IMAGE,
	                            adminPage.getResourceImagePage( page, strPageId ) );
	                    }
	                }
	
	                XmlUtil.endElement( strXml, TAG_CHILD_PAGE );
	            }
            }
        }

        XmlUtil.endElement( strXml, TAG_CHILD_PAGES_PORTLET_LIST );

        return addPortletTags( strXml );
    }

    /**
     * Returns the Xml code of the ChildPage portlet with XML heading
     *
     * @param request The HTTP Servlet Request
     * @return the Xml code of the ChildPage portlet
     */
    public String getXmlDocument( HttpServletRequest request )
    {
        return XmlUtil.getXmlHeader(  ) + getXml( request );
    }

    /**
     * Updates the current instance of the ChildPage Portlet object
     */
    public void update(  )
    {
        ChildPagesPortletHome.getInstance(  ).update( this );
    }

    /**
     * Removes the current instance of the ChildPage Portlet object
     */
    public void remove(  )
    {
        ChildPagesPortletHome.getInstance(  ).remove( this );
    }
}
