#!/usr/bin/env python
# -*- coding: utf-8 -*-
# -*- mode: python -*-

import postgres_setup, fedora_conn


postgres_setup.setup()
fedora_conn.test_fedora_conn()
