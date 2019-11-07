package com.queatz.fantasydating

const val AQL_PARAM_ENTITIES = "@entities"
const val AQL_PARAM_TOKEN = "token"
const val AQL_PARAM_PERSON = "person"

const val AQL_UPSERT_PERSON = """
    UPSERT { kind: 'person', token: @token }
        INSERT { kind: 'person', token: @token, created: DATE_ISO8601(DATE_NOW()), updated: DATE_ISO8601(DATE_NOW()) }
        UPDATE { updated: DATE_ISO8601(DATE_NOW()) }
        IN @@entities
        RETURN NEW
"""

const val AQL_DISCOVERY_PREFERENCES_FOR_PERSON = """
    FOR discoveryPreferences IN @@entities
        FILTER discoveryPreferences.person == @person
        RETURN discoveryPreferences
"""
