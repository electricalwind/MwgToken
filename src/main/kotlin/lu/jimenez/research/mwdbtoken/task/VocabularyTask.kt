package lu.jimenez.research.mwdbtoken.task

import lu.jimenez.research.mwdbtoken.Constants.*
import lu.jimenez.research.mwdbtoken.actions.MwdbTokenActions.retrieveVocabularyNode
import lu.jimenez.research.mylittleplugin.MyLittleActions.ifEmptyThen
import mu.KLogging
import org.mwg.*
import org.mwg.core.task.Actions.newTask
import org.mwg.task.*

object VocabularyTask : KLogging() {

    /**
     * Create a NodeIndex vocabulary
     */
    fun initializeVocabulary(): Task {
        return newTask()
                .createNode()
                .setAttribute(ENTRY_POINT_NODE_NAME, Type.STRING, VOCABULARY_NODE_NAME)
                .addToGlobalIndex(ENTRY_POINT_INDEX, ENTRY_POINT_NODE_NAME)
    }

    @JvmStatic
    fun getOrCreateTokensFromString(tokens: Array<String>): Task {
        return newTask()
                .then(retrieveVocabularyNode())
                .defineAsVar("Vocabulary")
                .inject(tokens)
                .forEach(retrieveToken())
    }

    private fun retrieveToken(): Task {
        return newTask()
                .defineAsVar("token")
                .readVar("Vocabulary")
                .traverse(VOCABULARY_TOKEN_INDEX, TOKEN_NAME, "token")
                .then(ifEmptyThen(
                        createToken("token")
                ))
    }

    private fun createToken(token: String): Task {
        return newTask()
                .createNode()
                .setAttribute(TOKEN_NAME, Type.STRING, "token")
                .addVarToRelation(VOCABULARY_TOKEN_INDEX, "Vocabulary", TOKEN_NAME)
                .defineAsVar("newToken")
                .createNode()
                .thenDo {
                    ActionFunction {
                        ctx: TaskContext ->
                        val invertedIndex = ctx.resultAsNodes()[0]
                        val newToken = ctx.variable("newToken")[0] as Node
                        invertedIndex.getOrCreate(INVERTED_INDEX_NODE_II, Type.LONG_TO_LONG_ARRAY_MAP)
                        invertedIndex.addToRelation(INVERTED_INDEX_WORD_RELATION, newToken)
                        newToken.addToRelation(WORD_INVERTED_INDEX_RELATION, invertedIndex)
                        ctx.continueTask()
                    }
                }

    }

}