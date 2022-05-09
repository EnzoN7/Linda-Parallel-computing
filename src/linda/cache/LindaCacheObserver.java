package linda.cache;

import linda.Linda;
import linda.Tuple;
import linda.evaluator.LindaEvaluation;
import linda.LindaObserver;

import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LindaCacheObserver implements LindaObserver {

    private ILindaCache cache;

    private Linda linda;

    public LindaCacheObserver(ILindaCache cache, Linda linda) {
        this.cache = cache;
        this.linda = linda;
    }

    @Override
    public Map<UUID, LindaEvaluation> getHistory() {
        return null;
    }

    @Override
    public void onWriteStart(UUID requestId, Tuple tuple) {

    }

    @Override
    public void onWriteEnd(UUID requestId, Tuple tuple) {

    }

    @Override
    public Optional<Tuple> onReadStart(UUID requestId, Tuple template) {
        Optional<Tuple> _tuple = cache.get(template);

        if(_tuple.isPresent()) {
            Tuple tuple = _tuple.get();

            if(linda.tryRead(tuple) != null) {
                return Optional.of(tuple);
            } else {
                cache.removeOne(template);

                // appel recursive (!)
                this.onReadStart(requestId, template);
            }
        }

        return Optional.empty();
    }

    @Override
    public void onReadEnd(UUID requestId, Tuple template, Tuple result) {
        cache.put(template, result);
    }

    @Override
    public void onTakeStart(UUID requestId, Tuple template) {

    }

    @Override
    public void onTakeEnd(UUID requestId, Tuple template, Tuple result) {
        Optional<Tuple> _tuple = cache.get(template);

        if(_tuple.isPresent()) {
            cache.removeOne(template);
        }
    }

    public Map<Tuple, Deque<Tuple>> getCache() {
        return cache.getMap();
    }

    public void clearCache() {
        cache.clear();
    }
}
