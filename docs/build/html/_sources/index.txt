.. opensearch documentation master file, created by
   sphinx-quickstart on Fri Nov 12 14:30:08 2010.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

=========================
Opensearch documentation!
=========================

Here you will find documentation regarding various aspects of the Opensearch 
project. It is a work in progress so please let the development team know if 
new or better documentations is needed. Send a email to opensearch.head@gmail.com 
in this regard.

.. toctree::
   :maxdepth: 2

   Fedora <fedora_dbc/fedora_dbc_intro>
   Solr <solr_dbc/solr_dbc_intro>
   Solr Regression Test <solr_dbc/solr_regression>
   Datadock <opensearch/datadock>
   DBC's Python Project <dbc_python/dbc_python_intro>

============================
Introduction to DBC's Fedora
============================

At DBC we use the Fedora Commons Repository in ways which have demanded that we patch
the source code and change some of the policy settings in the configuration of the 
repository. For a detailed description of how what changes we have made see here: 
:doc:`fedora_dbc/fedora_dbc_intro`.

==========================
Introduction to DBC's Solr
==========================

At DBC we use the Solr/Lucene... For further details pleaser refer to: 
:doc:`solr_dbc/solr_dbc_intro`.

====================================
Introduction to Solr Regression Test
====================================

An easy to use test framework used for testing a post from it is ingested into FCREPO 
until it is made searchable through a Lucene index. For further details please refer to:
:doc:`solr_dbc/solr_regression`

============================
Introduction to the Datadock
============================

The Datadock serves as an intermediate layer between the Fedora Commons Repository 
(FCREPO: http://www.fedora-commons.org/) and ingest as well as query services. It 
handles the bookkeeping of ingesting new post, updating previously ingested posts, and 
deleting outdated posts. For a detailed description see here: :doc:`opensearch/datadock`


====================================
Introduction to DBC's Python Project
====================================

At DBC we are striving to collect all reusable Python code developed at DBC in one 
common project. A very short description of the use of this project in Opensearch 
including relevant links can be found here: :doc:`dbc_python/dbc_python_intro`.
