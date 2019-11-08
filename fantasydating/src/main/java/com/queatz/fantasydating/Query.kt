package com.queatz.fantasydating

object AqlParam {
    const val Collection = "@collection"
    const val Graph = "graph"
    const val Token = "token"
    const val Person = "person"
    const val Id = "id"
    const val From = "from"
    const val To = "to"
}

object AqlQuery {
    const val UpsertPerson = """
        UPSERT { kind: 'person', token: @token }
            INSERT { kind: 'person', token: @token, created: DATE_ISO8601(DATE_NOW()), updated: DATE_ISO8601(DATE_NOW()) }
            UPDATE { updated: DATE_ISO8601(DATE_NOW()) }
            IN @@collection
            RETURN NEW
    """

    const val DiscoveryPreferencesForPerson = """
        FOR discoveryPreferences IN @@collection
            FILTER discoveryPreferences.kind == 'discovery-preferences'
            FILTER discoveryPreferences.person == @person
            RETURN discoveryPreferences
    """

    const val EventsForPerson = """
        FOR event IN @@collection
            FILTER event.kind == 'event'
            FILTER event.person == @person
            SORT DATE_TIMESTAMP(event.created) DESC
            LIMIT 20
            RETURN event
    """

    const val MessagesBetweenPeople = """
        FOR message IN @@collection
            FILTER message.kind == 'message'
            FILTER (
                (message.from == @person AND message.to == @id)
                OR
                (message.from == @id AND message.to == @person)
            )
            SORT DATE_TIMESTAMP(message.created) DESC
            LIMIT 20
            RETURN message
    """

    const val PeopleForPerson = """
        FOR person IN @@collection
            FILTER person.kind == 'person'
                AND person._key != @person
                AND LENGTH(
                    FOR personHide IN ANY person GRAPH @graph
                        FILTER personHide._key == @person RETURN true
                ) == 0
                SORT DATE_TIMESTAMP(person.seen) DESC
                LIMIT 20
                RETURN person
    """

    const val IsPersonHiddenForPerson = """
        RETURN LENGTH(
            FOR personHide IN ANY @id GRAPH @graph
                FILTER personHide._key == @person RETURN true
        ) != 0
    """

    const val ById = "FOR x IN @@collection FILTER x._key == @id RETURN x"

    const val RemoveLove = """
        FOR love IN @@collection FILTER love.kind == 'love' AND love._from == @from AND love._to == @to
            REMOVE love IN @@collection OPTIONS { ignoreErrors: true }
            RETURN love
    """

    const val AddLove = """UPSERT { kind: 'love', _from: @from, _to: @to }
        INSERT { kind: 'love', _from: @from, _to: @to, meet: @meet, created: DATE_ISO8601(DATE_NOW()), updated: DATE_ISO8601(DATE_NOW()) }
            IN @@collection
            RETURN NEW
    """

    const val HidePerson = """UPSERT { kind: 'hide', _from: @from, _to: @to }
        INSERT { kind: 'hide', _from: @from, _to: @to, meet: @meet, created: DATE_ISO8601(DATE_NOW()), updated: DATE_ISO8601(DATE_NOW()) }
            IN @@collection
            RETURN NEW
    """
}