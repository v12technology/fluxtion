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

import com.mxgraph.io.mxGraphMlCodec;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility method for generating png representation of a graph from a graphml
 * source.
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class PngGenerator {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PngGenerator.class);

    public static void generatePNG(File graphmlFile, File pngFile) {
        mxGraph graph = new mxGraph();
        mxConstants.SPLIT_WORDS = false;
        //style
        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_ROUNDED, true);
        style.put(mxConstants.STYLE_OPACITY, 100);
        style.put(mxConstants.STYLE_FILLCOLOR, "#53B9F0");
        style.put(mxConstants.STYLE_FONTCOLOR, "black");
        style.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
//        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_FONTFAMILY, "Segoe");
        stylesheet.putCellStyle("EVENTHANDLER", style);

        style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_ROUNDED, true);
        style.put(mxConstants.STYLE_OPACITY, 100);
        style.put(mxConstants.STYLE_FILLCOLOR, "#ffbf80");
        style.put(mxConstants.STYLE_FONTCOLOR, "black");
        style.put(mxConstants.STYLE_FONTFAMILY, "Segoe");
        style.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        style.put(mxConstants.STYLE_AUTOSIZE, 1);
        stylesheet.putCellStyle("EVENT", style);
        stylesheet.putCellStyle("EXPORTSERVICE", style);

        style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
        style.put(mxConstants.STYLE_ROUNDED, true);
        style.put(mxConstants.STYLE_OPACITY, 100);
        mxConstants.SPLIT_WORDS = false;
        style.put(mxConstants.STYLE_FONTCOLOR, "black");
//        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_FILLCOLOR, "#53c68c");
        style.put(mxConstants.STYLE_FONTFAMILY, "Segoe");
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        style.put(mxConstants.STYLE_AUTOSIZE, 1);
        stylesheet.putCellStyle("NODE", style);

        style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CLOUD);
        style.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
        style.put(mxConstants.STYLE_ROUNDED, true);
        style.put(mxConstants.STYLE_OPACITY, 100);
        mxConstants.SPLIT_WORDS = false;
        style.put(mxConstants.STYLE_FONTCOLOR, "red");
//        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_FILLCOLOR, "#346789");
        style.put(mxConstants.STYLE_FONTFAMILY, "Segoe");
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        style.put(mxConstants.STYLE_AUTOSIZE, 1);
        stylesheet.putCellStyle("SELECTED", style);
        try {
            Document document = null;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            document = parser.parse(graphmlFile);
            Object parent = graph.getDefaultParent();
            mxGraphMlCodec.decode(document, graph);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(PngGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        //create graph and save to file
        graph.setCellsEditable(false);
        graph.setCellsMovable(false);
        graph.setCellsResizable(false);
        graph.setCellsDisconnectable(false);
        graph.setCellsLocked(true);
        graph.setConnectableEdges(false);
        Object parent = graph.getDefaultParent();
        mxHierarchicalLayout layoutImpl = new mxHierarchicalLayout(graph);
        layoutImpl.setInterRankCellSpacing(80);
        layoutImpl.setIntraCellSpacing(70);
        layoutImpl.setFineTuning(true);
        layoutImpl.execute(parent);
        //save image
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
        if (pngFile.getParentFile() != null && pngFile.getParentFile().mkdirs()) {
        } else {
            if (!pngFile.getParentFile().exists()) {
                LOG.error("failed to make parent directories for image output");
            }
        }
        if (image == null) {
            LOG.info("no png image generated, need more than one node in sep");
        } else {
            try {
                ImageIO.write(image, "PNG", pngFile);
                LOG.debug("png image generated:{}", pngFile.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(PngGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
