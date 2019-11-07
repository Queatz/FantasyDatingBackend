package com.queatz.fantasydating

import com.arangodb.ArangoDB
import com.arangodb.ArangoDBException
import com.arangodb.ArangoDatabase
import com.arangodb.entity.CollectionType
import com.arangodb.entity.EdgeDefinition
import com.arangodb.model.*
import com.arangodb.velocypack.module.jdk8.VPackJdk8Module
import com.queatz.fantasydating.util.Time
import com.queatz.on.On
import java.io.IOException
import java.util.*
import java.util.logging.Logger

class Arango constructor(private val on: On) {

    private val collection: String = DB_COLLECTION_ENTITIES
    private val edges: String = DB_COLLECTION_EDGES
    private val graph: String = DB_GRAPH

    private val isVolatile: Boolean = false
    private val printAql: Boolean = false

    private lateinit var arangoDatabase: ArangoDatabase

    private fun db() = if (this::arangoDatabase.isInitialized.not()) {
        arangoDatabase = ArangoDB.Builder()
            .user(DB_USER)
            .password(DB_PASS)
            .acquireHostList(false)
            .registerModule(VPackJdk8Module())
            .build()
            .db(DB_DATABASE).apply {
                try {
                    createCollection(
                        collection,
                        CollectionCreateOptions().isVolatile(isVolatile)
                    )
                } catch (ignored: ArangoDBException) {
                    // Whatever
                }

                try {
                    createCollection(
                        edges,
                        CollectionCreateOptions().type(CollectionType.EDGES).isVolatile(isVolatile)
                    )
                } catch (ignored: ArangoDBException) {
                    // Whatever
                }

                try {
                    val edgeDefinitions = ArrayList<EdgeDefinition>()
                    edgeDefinitions.add(
                        EdgeDefinition().collection(edges).from(collection).to(
                            collection
                        )
                    )
                    createGraph(graph, edgeDefinitions, GraphCreateOptions())
                } catch (ignored: ArangoDBException) {
                    // Whatever
                }

                listOf("kind").forEach { field ->
                    val index = HashSet<String>()
                    index.add(field)
                    collection(DB_COLLECTION_ENTITIES).ensureHashIndex(index, HashIndexOptions())
                }
            }

        arangoDatabase
    } else arangoDatabase

    fun <T> queryOne(aql: String, params: MutableMap<String, Any>, clazz: Class<T>): T? {
        if (printAql) {
            Logger.getGlobal().info(aql)
        }

        try {
            db().query(aql, params, null, clazz).use { cursor -> return if (cursor.hasNext()) cursor.next() else null }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    fun <T> query(aql: String, params: MutableMap<String, Any>, clazz: Class<T>): List<T> {
        if (printAql) {
            Logger.getGlobal().info(aql)
        }

        try {
            db().query(aql, params, null, clazz).use { cursor -> return cursor.asListRemaining() }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    fun ensureId(key: String): String {
        return if (key.startsWith("$collection/")) key else "$collection/$key"
    }

    fun ensureKey(id: String): String {
        val sep = id.indexOf("/")
        return if (sep != -1) id.substring(sep + 1) else id
    }

    fun <T : BaseModel> save(model: T): T? {
        model.updated = on<Time>().now()

        return try {
            if (model.id == null) {
                model.created = on<Time>().now()

                on<Arango>().db().collection(DB_COLLECTION_ENTITIES).insertDocument(model, DocumentCreateOptions().returnNew(true)).new
            } else {
                on<Arango>().db().collection(DB_COLLECTION_ENTITIES).updateDocument(model.id, model, DocumentUpdateOptions().returnNew(true)).new
            }
        } catch (e: ArangoDBException) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val DB_USER = "fantasy"
        private const val DB_PASS = "fantasy"
        private const val DB_DATABASE = "fantasy"

        private const val DB_COLLECTION_ENTITIES = "entities"
        private const val DB_COLLECTION_EDGES = "edges"
        private const val DB_GRAPH = "graph"
    }
}
