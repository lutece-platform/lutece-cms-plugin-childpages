-- liquibase formatted sql
-- changeset childpages:update_db_core_childpages-5.0.1-6.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'core_portlet' AND column_name = 'id_template'
--
-- The XSL based rendering has been removed : every child pages portlet is now rendered with a FreeMarker template
-- chosen per portlet among the templates registered in the core for the portlet type (core_portlet_template,
-- core_portlet.id_template, Section Template Management feature). The old XSL styles are mapped to the matching templates.
--
-- The plugin upgrade scripts run BEFORE the core upgrade script in the same liquibase run (sql/plugins/* sorts before sql/upgrade/*) :
-- the core structures are created here when they do not exist yet, with the very same statements as the core script, which is then skipped.
--
ALTER TABLE core_portlet ADD COLUMN id_template int default 0 NOT NULL;

-- changeset childpages:update_db_core_childpages-5.0.1-6.0.0.sql-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'core_portlet_template'
CREATE TABLE IF NOT EXISTS core_portlet_template (
	id_template int AUTO_INCREMENT NOT NULL,
	id_portlet_type varchar(50) default NULL,
	description varchar(255) default NULL,
	template_path varchar(255) default NULL,
	PRIMARY KEY (id_template)
);

--
-- Templates available for the child pages portlets
--
-- changeset childpages:update_db_core_childpages-5.0.1-6.0.0.sql-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'CHILDPAGES_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('CHILDPAGES_PORTLET', 'Défaut', 'skin/plugins/childpages/portlet/childpages_portlet.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('CHILDPAGES_PORTLET', 'Image + lien', 'skin/plugins/childpages/portlet/childpages_portlet_image.html');
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('CHILDPAGES_PORTLET', 'Encadré', 'skin/plugins/childpages/portlet/childpages_portlet_boxed.html');

--
-- Template chosen for each portlet : old "Image + lien" XSL style (301) -> "Image + lien" template, other styles -> default template (0)
--
-- changeset childpages:update_db_core_childpages-5.0.1-6.0.0.sql-rev3.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_portlet SET id_template = (SELECT MIN(id_template) FROM core_portlet_template WHERE id_portlet_type = 'CHILDPAGES_PORTLET' AND template_path = 'skin/plugins/childpages/portlet/childpages_portlet_image.html') WHERE id_portlet_type = 'CHILDPAGES_PORTLET' AND id_style = 301;
UPDATE core_portlet SET id_style = 0 WHERE id_portlet_type = 'CHILDPAGES_PORTLET';

-- changeset childpages:update_db_core_childpages-5.0.1-6.0.0.sql-rev4.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Legacy XSL style tables left the core for plugin-xmltransformer and are absent from many databases: skip instead of failing the whole update
-- precondition-sql-check expectedResult:3 SELECT COUNT(1) from INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=database() AND TABLE_NAME IN ('core_style_mode_stylesheet','core_stylesheet','core_style');
DELETE FROM core_style_mode_stylesheet WHERE id_style IN (300, 301);
DELETE FROM core_style WHERE id_style IN (300, 301);
DELETE FROM core_stylesheet WHERE id_stylesheet IN (30, 9006);

--
-- Development databases only : the plugin-owned template registry of the 6.0.0-SNAPSHOT builds (never released) is migrated to the core one, then dropped
--
-- changeset childpages:update_db_core_childpages-5.0.1-6.0.0.sql-rev5.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'childpages_portlet_template'
UPDATE core_portlet SET id_template = (
		SELECT MIN(ct.id_template) FROM core_portlet_template ct, childpages_portlet_template pt, childpages_portlet cp
		WHERE cp.id_portlet = core_portlet.id_portlet AND pt.id_template = cp.id_template
		AND ct.id_portlet_type = 'CHILDPAGES_PORTLET' AND ct.template_path = pt.template_path )
	WHERE id_portlet_type = 'CHILDPAGES_PORTLET'
	AND id_portlet IN (SELECT cp2.id_portlet FROM childpages_portlet cp2, childpages_portlet_template pt2, core_portlet_template ct2 WHERE pt2.id_template = cp2.id_template AND ct2.id_portlet_type = 'CHILDPAGES_PORTLET' AND ct2.template_path = pt2.template_path);
DROP TABLE childpages_portlet_template;
ALTER TABLE childpages_portlet DROP COLUMN id_template;
DELETE FROM core_user_right WHERE id_right = 'CHILDPAGES_PORTLET_TEMPLATE_MANAGEMENT';
DELETE FROM core_admin_right WHERE id_right = 'CHILDPAGES_PORTLET_TEMPLATE_MANAGEMENT';
DELETE FROM core_admin_role_resource WHERE resource_type = 'CHILDPAGES_PORTLET_TEMPLATE';
