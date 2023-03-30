/*
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.compiler.generation.exporter;

import com.fluxtion.runtime.node.EventHandlerNode;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.jgrapht.Graph;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.SimpleGraph;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;


/**
 * Exports a graph into a GraphML file.
 *
 * <p>
 * For a description of the format see <a
 * href="http://en.wikipedia.org/wiki/GraphML">
 * http://en.wikipedia.org/wiki/GraphML</a>.</p>
 *
 * @author Trevor Harmon
 */
public class JgraphGraphMLExporter<V, E> {

    //~ Instance fields --------------------------------------------------------
    private final VertexNameProvider<V> vertexIDProvider;
    private final VertexNameProvider<V> vertexLabelProvider;
    private final EdgeNameProvider<E> edgeIDProvider;
    private final EdgeNameProvider<E> edgeLabelProvider;

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructs a new GraphMLExporter object with integer name providers for
     * the vertex and edge IDs and null providers for the vertex and edge
     * labels.
     */
    public JgraphGraphMLExporter() {
        this(
                new IntegerNameProvider<>(),
                null,
                new IntegerEdgeNameProvider<>(),
                null);
    }

    /**
     * Constructs a new GraphMLExporter object with the given ID and label
     * providers.
     *
     * @param vertexIDProvider    for generating vertex IDs. Must not be null.
     * @param vertexLabelProvider for generating vertex labels. If null, vertex
     *                            labels will not be written to the file.
     * @param edgeIDProvider      for generating vertex IDs. Must not be null.
     * @param edgeLabelProvider   for generating edge labels. If null, edge labels
     *                            will not be written to the file.
     */
    public JgraphGraphMLExporter(
            VertexNameProvider<V> vertexIDProvider,
            VertexNameProvider<V> vertexLabelProvider,
            EdgeNameProvider<E> edgeIDProvider,
            EdgeNameProvider<E> edgeLabelProvider) {
        this.vertexIDProvider = vertexIDProvider;
        this.vertexLabelProvider = vertexLabelProvider;
        this.edgeIDProvider = edgeIDProvider;
        this.edgeLabelProvider = edgeLabelProvider;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Exports a graph into a plain text file in GraphML format.
     *
     * @param writer the writer to which the graph to be exported
     * @param g      the graph to be exported
     * @throws org.xml.sax.SAXException                              exception during reading
     * @throws javax.xml.transform.TransformerConfigurationException exception during reading
     */
    public void export(Writer writer, Graph<V, E> g)
            throws SAXException, TransformerConfigurationException {
        // Prepare an XML file to receive the GraphML data
        PrintWriter out = new PrintWriter(writer);
        StreamResult streamResult = new StreamResult(out);
        SAXTransformerFactory factory
                = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler handler = factory.newTransformerHandler();
        Transformer serializer = handler.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        handler.setResult(streamResult);
        handler.startDocument();
        AttributesImpl attr = new AttributesImpl();

        // <graphml>
        handler.startPrefixMapping(
                "xsi",
                "http://www.w3.org/2001/XMLSchema-instance");

        // FIXME: Is this the proper way to add this attribute?
        attr.addAttribute(
                "",
                "",
                "xsi:schemaLocation",
                "CDATA",
                "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");

        attr.addAttribute(
                "",
                "",
                "xmlns:jGraph",
                "CDATA",
                "http://www.jgraph.com/");


        handler.startElement(
                "http://graphml.graphdrawing.org/xmlns",
                "",
                "graphml",
                attr);
        handler.endPrefixMapping("xsi");

        if (vertexLabelProvider != null) {
            // <key> for vertex label attribute
            attr.clear();
            attr.addAttribute("", "", "id", "CDATA", "vertex_label");
            attr.addAttribute("", "", "for", "CDATA", "node");
            attr.addAttribute("", "", "attr.name", "CDATA", "nodeData");
            attr.addAttribute("", "", "attr.type", "CDATA", "string");
            handler.startElement("", "", "key", attr);
            handler.endElement("", "", "key");
        }

        if (edgeLabelProvider != null) {
            // <key> for edge label attribute
            attr.clear();
            attr.addAttribute("", "", "id", "CDATA", "edge_label");
            attr.addAttribute("", "", "for", "CDATA", "edge");
            attr.addAttribute("", "", "attr.name", "CDATA", "edgeData");
            attr.addAttribute("", "", "attr.type", "CDATA", "string");
            handler.startElement("", "", "key", attr);
            handler.endElement("", "", "key");
        }

        // <graph>
        attr.clear();
        attr.addAttribute(
                "",
                "",
                "edgedefault",
                "CDATA",
                (g instanceof SimpleGraph<?, ?>) ? "directed" : "undirected");
        handler.startElement("", "", "graph", attr);

        // Add all the vertices as <node> elements...
        for (V v : g.vertexSet()) {
            boolean isHandler = v instanceof EventHandlerNode;
            boolean isEventClass = v instanceof Class;
            if (!isHandler) {
                Method[] methodList = v.getClass().getMethods();
                for (Method method : methodList) {
                    if (method.getAnnotation(OnEventHandler.class) != null) {
                        isHandler = true;
                        break;
                    }
                }
            }
            // <node>
            attr.clear();
            attr.addAttribute(
                    "",
                    "",
                    "id",
                    "CDATA",
                    vertexIDProvider.getVertexName(v));
            handler.startElement("", "", "node", attr);

            if (vertexLabelProvider != null) {
                // <data>
                attr.clear();
                attr.addAttribute("", "", "key", "CDATA", "vertex_label");
                handler.startElement("", "", "data", attr);
                //<jGraph:ShapeNode>
                handler.startElement("", "", "jGraph:ShapeNode", null);

                // Content for <data>
                String vertexLabel = vertexLabelProvider.getVertexName(v);
                //<jGraph:Geometry height="30.0" width="80.0" x="20.0" y="20.0"/>
                attr.clear();
                attr.addAttribute("", "", "height", "CDATA", "70");
                attr.addAttribute("", "", "width", "CDATA", "160");
                attr.addAttribute("", "", "x", "CDATA", "20");
                attr.addAttribute("", "", "y", "CDATA", "20");
                handler.startElement("", "", "jGraph:Geometry", attr);
                handler.endElement("", "", "jGraph:Geometry");
                //<jGraph:label text="Hello"/>
                attr.clear();
                if (isHandler) {
                    attr.addAttribute("", "", "text", "CDATA", "<<EventHandle>>\n"
                            + vertexLabel + ":\n"
                            + v.getClass().getSimpleName()
                    );
                } else if (isEventClass) {
                    attr.addAttribute("", "", "text", "CDATA", "<<Event>>\n"
                            + vertexLabel
                    );
                } else {
                    attr.addAttribute("", "", "text", "CDATA", ""
                            + vertexLabel + ":\n"
                            + v.getClass().getSimpleName()
                    );
                }
                handler.startElement("", "", "jGraph:label", attr);
                handler.endElement("", "", "jGraph:label");


                attr.clear();
                if (isHandler) {
                    attr.addAttribute("", "", "properties", "CDATA", "EVENTHANDLER");
                } else if (isEventClass) {
                    attr.addAttribute("", "", "properties", "CDATA", "EVENT");
                } else {
                    attr.addAttribute("", "", "properties", "CDATA", "NODE");
                }
                handler.startElement("", "", "jGraph:Style", attr);
                handler.endElement("", "", "jGraph:label");
                //</jGraph:ShapeNode> 
                handler.endElement("", "", "jGraph:ShapeNode");


                handler.endElement("", "", "data");
            }

            handler.endElement("", "", "node");
        }

        // Add all the edges as <edge> elements...
        for (E e : g.edgeSet()) {
            // <edge>
            attr.clear();
            attr.addAttribute(
                    "",
                    "",
                    "id",
                    "CDATA",
                    edgeIDProvider.getEdgeName(e));
            attr.addAttribute(
                    "",
                    "",
                    "source",
                    "CDATA",
                    vertexIDProvider.getVertexName(g.getEdgeSource(e)));
            attr.addAttribute(
                    "",
                    "",
                    "target",
                    "CDATA",
                    vertexIDProvider.getVertexName(g.getEdgeTarget(e)));
            handler.startElement("", "", "edge", attr);

            if (edgeLabelProvider != null) {
                // <data>
                attr.clear();
                attr.addAttribute("", "", "key", "CDATA", "edge_label");
                handler.startElement("", "", "data", attr);

                handler.startElement("", "", "jGraph:ShapeEdge", null);
                handler.endElement("", "", "jGraph:ShapeEdge");

                handler.endElement("", "", "data");
            }

            handler.endElement("", "", "edge");
        }

        handler.endElement("", "", "graph");
        handler.endElement("", "", "graphml");
        handler.endDocument();
        out.flush();
    }
}
