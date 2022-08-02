package nl.backbase.mapper;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component
public interface Mapper<S, T> {
    default Collection<T> map(final Collection<S> sCol) {
        if (sCol == null) return Collections.emptyList();
        final var tCol = new ArrayList<T>();
        sCol.forEach(s -> tCol.add(map(s)));
        return tCol;
    }

    T map(final S s);
}
