package lu.jimenez.research.mwdbtoken.nlp.corpus.actions;

import lu.jimenez.research.mwdbtoken.nlp.corpus.task.CorpusTask;
import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.internal.task.TaskHelper;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static lu.jimenez.research.mwdbtoken.nlp.corpus.actions.MwdbCorpusActionNames.ADD_REMOVE_TOKENIZE_CONTENTS_OF_CORPUS;

public class ActionAddRemoveTokenizeContentOfCorpus implements Action{

    private final boolean _add;
    private final String _tokenizeContentVar;
    private final String _corpusName;

    public ActionAddRemoveTokenizeContentOfCorpus(boolean p_add, String p_tokenizeContentVar, String p_corpusName){
        this._add = p_add;
        this._tokenizeContentVar = p_tokenizeContentVar;
        this._corpusName = p_corpusName;

    }

    @Override
    public void eval(TaskContext ctx) {
        final Task task;
        if(_add){
           task = CorpusTask.addTokenizeContentToCorpus(_tokenizeContentVar,_corpusName);
        }
        else{
            task = CorpusTask.removeTokenizeContentFromCorpus(_tokenizeContentVar,_corpusName);
        }
        task.executeFrom(ctx, ctx.result(), SchedulerAffinity.SAME_THREAD,
                new Callback<TaskResult>() {
                    public void on(TaskResult res) {
                        Exception exceptionDuringTask = null;
                        if (res != null) {
                            if (res.output() != null) {
                                ctx.append(res.output());
                            }
                            if (res.exception() != null) {
                                exceptionDuringTask = res.exception();
                            }
                        }
                        if (exceptionDuringTask != null) {
                            ctx.endTask(res, exceptionDuringTask);
                        } else {
                            ctx.continueWith(res);
                        }
                    }
                });
    }

    public void serialize(StringBuilder builder) {
        builder.append(ADD_REMOVE_TOKENIZE_CONTENTS_OF_CORPUS);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(_add);
        builder.append(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeString(_tokenizeContentVar, builder, true);
        builder.append(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeString(_corpusName, builder, true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }
}