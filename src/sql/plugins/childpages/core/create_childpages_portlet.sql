--
-- Structure for table childpages_portlet
--

DROP TABLE IF EXISTS childpages_portlet;
CREATE TABLE childpages_portlet (
  id_portlet int DEFAULT '0' NOT NULL,
  id_child_page int DEFAULT '0' NOT NULL,
  PRIMARY KEY (id_portlet,id_child_page)
);

--
-- Dumping data for table childpages_portlet
--
