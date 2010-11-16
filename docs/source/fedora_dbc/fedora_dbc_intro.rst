============================
Introduction to DBC's Fedora
============================

At DBC we have used the Fedora Commons Repository (FCREPO) since version 3.2

-------------
Version 3.4.1
-------------

2010.11.15: In version 3.4.1 of FCREPO a security bug was fixed. Due to this and in order 
to make future upgrade process as easy as possible, an integration of FCREPO v3.4.1 was 
begun.

-----------
Version 3.4
-----------

2010.10.01: During the work with an integration of Solr using Tika to index posts from 
FCREPO it was made clear that we had to make changes to how policies where handled. Due to 
this version 3.4 were experimented with.

-----------
Version 3.3
-----------

2010.05.01: As of version 3.3 DBC began to integrate FCREPO into a setup using the Solr 
framework. It was soon made clear that the repository had a number of short comings in 
such a setup. A number of work arounds where added amongst which was the following 
patches, which are found in svn `fedora-dbc/trunk/patches/fedora-3.3/`:

       * FCREPO-711:
       * FCREPO-716:
       * FCREPO-723:  
       * FCREPO-731:
       * FCREPO-781:

-----------
Version 3.2
-----------

2008: No changes were made to FCREPO v.3.2. It was used out of the box and 
the various available ways to query the repository was explored.
