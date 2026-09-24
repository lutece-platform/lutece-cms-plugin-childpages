-- liquibase formatted sql
-- changeset childpages:init_core_childpage.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
--

--
-- Dumping data for table core_portlet_type
--
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
 ('CHILDPAGES_PORTLET','childpages.portlet.name','plugins/childpages/CreatePortletChildPages.jsp','plugins/childpages/ModifyPortletChildPages.jsp','fr.paris.lutece.plugins.childpages.business.portlet.ChildPagesPortletHome','childpages','plugins/childpages/DoCreatePortletChildPages.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/childpages/value_id_parent.html','','plugins/childpages/DoModifyPortletChildPages.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/childpages/value_id_parent.html','');

--
-- FreeMarker templates available for the child pages portlets (6.0.0), registered in the core (Section Template Management feature)
--
-- changeset childpages:init_core_childpage.sql-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'CHILDPAGES_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('CHILDPAGES_PORTLET', 'Défaut', 'skin/plugins/childpages/portlet/childpages_portlet.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('CHILDPAGES_PORTLET', 'Image + lien', 'skin/plugins/childpages/portlet/childpages_portlet_image.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('CHILDPAGES_PORTLET', 'Encadré', 'skin/plugins/childpages/portlet/childpages_portlet_boxed.html');
