package net.fortytwo.extendo.server;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.rexster.RexsterResourceContext;
import com.tinkerpop.rexster.extension.ExtensionDefinition;
import com.tinkerpop.rexster.extension.ExtensionDescriptor;
import com.tinkerpop.rexster.extension.ExtensionNaming;
import com.tinkerpop.rexster.extension.ExtensionPoint;
import com.tinkerpop.rexster.extension.ExtensionRequestParameter;
import com.tinkerpop.rexster.extension.ExtensionResponse;
import com.tinkerpop.rexster.extension.RexsterContext;
import net.fortytwo.extendo.brain.Note;
import org.json.JSONException;

import java.io.IOException;

/**
 * A service for finding root nodes of an Extend-o-Brain graph
 * @author Joshua Shinavier (http://fortytwo.net)
 */
@ExtensionNaming(namespace = "extendo", name = "find-roots")
//@ExtensionDescriptor(description = "find root nodes of an Extend-o-Brain graph")
public class FindRootsExtension extends ExtendoExtension {

    @ExtensionDefinition(extensionPoint = ExtensionPoint.GRAPH)
    @ExtensionDescriptor(description = "an extension for finding root nodes of an Extend-o-Brain graph")
    public ExtensionResponse handleRequest(@RexsterContext RexsterResourceContext context,
                                           @RexsterContext Graph graph,
                                           @ExtensionRequestParameter(name = "request", description = "request description (JSON object)") String request) {
        Params p = createParams(context, (KeyIndexableGraph) graph);
        BasicViewRequest r;
        try {
            r = new BasicViewRequest(request, p.user);
        } catch (JSONException e) {
            return ExtensionResponse.error(e.getMessage());
        }

        //logInfo("extendo find-roots");

        p.depth = r.depth;
        p.styleName = r.styleName;
        p.filter = r.filter;

        return handleRequestInternal(p);
    }

    protected ExtensionResponse performTransaction(final Params p) throws Exception {
        addRoots(p);

        p.map.put("title", "all roots");
        return ExtensionResponse.ok(p.map);
    }

    protected boolean doesRead() {
        return true;
    }

    protected boolean doesWrite() {
        return false;
    }

    protected void addRoots(final Params p) throws IOException {
        Note n = p.queries.findRoots(p.filter, p.style, p.depth - 1);
        addView(n, p);
    }
}