/**
 * Created by Adnan on 8/24/2015.
 */
var visualizerOriginalHeight = 575;

var Visualizer = {
    /// GLOBALS ///
    //height = 755, width = 1360;
    width: 948,
    height: visualizerOriginalHeight,
    nodeMinRadius: 20,
    nodeMaxRadius: 75,
    nodePadding: 5,
    forceLayout: null,

    updateLayout: function (dataModel) {
        // The self is not actually needed here as the object itself is a singleton defined in the global scope by
        // the name of Visualizer
        var self = this;

        if(self.forceLayout) {
            console.log("Stopping the layout");
            self.forceLayout.stop();
        }

        /// Define Colours ///
        var dimmedLinkStroke = "#E0DFDB";
        var dimmedLinkStrokeOpacity = 0.6;
        var highlightedLinkStroke = "#6B6B6B";

        /// Define scales ///
        //http://www.december.com/html/spec/color1.html
        var colorScale = d3.scale.linear()
            .domain([d3.min(dataModel.nodes, function (d) {
                return (d.hs_weight);
            }),
                d3.mean(dataModel.nodes, function (d) {
                    return (d.hs_weight);
                }),
                d3.max(dataModel.nodes, function (d) {
                    return (d.hs_weight);
                })])
            .range(["#C8F526",
                "#EEEE00",
                "#E3170D"]);

        var radiusScale = d3.scale.linear()
            .domain([d3.min(dataModel.nodes, function (d) {
                return (d.hs_weight);
            }),
                d3.max(dataModel.nodes, function (d) {
                    return (d.hs_weight);
                })])
            .range([Visualizer.nodeMinRadius,
                Visualizer.nodeMaxRadius]);

        var linkStrokeWidthScale = d3.scale.linear()
            .domain([d3.min(dataModel.links, function (d) {
                return (d.baseJoin["Join Usage Score"])
            }),
                d3.max(dataModel.links, function (d) {
                    return (d.baseJoin["Join Usage Score"])
                })])
            .range([1, 10]);

        /// *** Start Drawing *** ///
        d3.select("#canvas-holder").selectAll('*').remove();

        var svg = d3.select("#canvas-holder")
            .append("svg")
            .attr("height", self.height)
            .attr("width", self.width)
            .style("border", "1px solid");

        var defs = svg.append("defs");

        /// Define Gradients ///
        var radialGradients = defs.selectAll("radialGradient")
            .data(dataModel.nodes)
            .enter()
            .append("radialGradient")
            .attr({
                id: function (d) {
                    // Todo: This properties should be NOT space separated
                    return ("rg_" + d.baseTable["Schema Name"] + "_" + d.baseTable["Table Name"]);
                },
                fx: "70%",
                fy: "30%",
                r: "100%",
                speadMethod: "pad"
            });
        radialGradients.append("stop")
            .attr("offset", "0%")
            .attr("stop-color", function (d) {
                var originalColor = d3.hsl(colorScale(d.hs_weight));
                return (d3.hsl(originalColor.h, originalColor.s, originalColor.l + 0.25));
            });
        radialGradients.append("stop")
            .attr("offset", "100%")
            .attr("stop-color", function (d) {
                return (colorScale(d.hs_weight));
                //var originalColor = d3.hsl(colorScale(d.hs_weight));
                //return (d3.hsl(originalColor.h, originalColor.s - 0.1, originalColor.l - 0.1));
            });

        /// Initialize Force Layout ///
        var fLayout = self.forceLayout = d3.layout.force()
            .nodes(dataModel.nodes)
            .links(dataModel.links)
            .size([Visualizer.width, Visualizer.height])
            .charge(-75)
            .on("tick", tick)
            .on("start", startForceLayout)
            .on("end", endForceLayout)
            .linkStrength(0)
            .linkDistance(200)
            .gravity(0.01)
            .start();

        /// Init Drag Behaviour ///
        var nodeIsBeingDragged = false;
        var flDrag = fLayout.drag()
            .on("dragstart", function (d) {
                nodeIsBeingDragged = true;
                d3.select(this).classed("fixed", d.fixed = true);
            })
            .on("dragend", function (d) {
                //console.log("Drag End");
                nodeIsBeingDragged = false;
                d3.event.sourceEvent.stopPropagation();
            });

        var visLine = null,linkText = null,visLinks = null, visNodes = null, visCircle = null, visTitle = null, visInfoIcon = null;

        /// Create the Visual links ///

        /// Join Score ///

        visLine = svg.selectAll(".link-line")
            .data(dataModel.links, uniqueID)
            .enter()
            .append("g")
            .classed("link-line", true)


        visLinks = visLine.append("line")
            .classed("link", true)
            .attr({
                x1: function (d) {
                    return (d.source.x);
                },
                y1: function (d) {
                    return (d.source.y);
                },
                x2: function (d) {
                    return (d.target.x);
                },
                y2: function (d) {
                    return (d.target.y);
                },
                stroke: dimmedLinkStroke,
                "stroke-width": function (d) {
                    return(linkStrokeWidthScale(d.baseJoin["Join Usage Score"]));
                    //return (2);
                },
                "stroke-opacity": dimmedLinkStrokeOpacity
            })
            .on("mouseover", function (d) {
                //console.log("Mouseover link", d);
            });

        // create a link lable
        linkText = visLine.append("text")
            .attr("class", "link-label")
            .attr("font-family", "Arial, Helvetica, sans-serif")
            .attr("fill", "Black")
            .style("font", "normal 12px Arial")

            .attr("dy", ".35em")
            .attr("text-anchor", "inherit")
            .text(function(d) {
                return d.baseJoin["Join Usage Score"];
            })
            .style("cursor", "hand");

        linkText
            .attr("x", function(d) {
                return ((d.source.x + d.target.x)/2);
            })
            .attr("y", function(d) {
                return ((d.source.y + d.target.y)/2);
            });

        //.style("display", "none");

        /// Create the Visual Nodes ///
        visNodes = svg.selectAll(".node")
            .data(dataModel.nodes, uniqueID)
            .enter()
            .append("g")
            .classed("node", true)
            .on("dblclick", function (d) {
                d.fixed = !d.fixed;
            })
            .on("click", nodeClicked)
            .on("mouseover", onNodeMouseOver)
            .on("mouseout", onNodeMouseOut)
            .call(flDrag);

        /// Create the Visual Circles ///
        visCircle = visNodes
            .append("circle")
            .classed("nodeShape", true)
            .attr({
                r: function (d) {
                    return (radiusScale(d.hs_weight));
                },
                fill: function (d) {
                    //return (colorScale(d.hs_weight));
                    return ("url(#rg_" + d.baseTable["Schema Name"] + "_" + d.baseTable["Table Name"] + ")");
                },
                //"fill-opacity":.9,
                stroke: function (d) {
                    var col = colorScale(d.hs_weight);
                    var darker = d3.rgb(col).darker(1.5);
                    return (darker);
                },
                "stroke-width": 1.5
            })


        /// Node Names ///
        visTitle = visNodes.append("text")
            .classed("nodeText", true)
            .attr({
                "fill": "black",
                "text-anchor": "middle",
                "cursor": "default",
                "font-size": "10px",
                y: function (d) {
                    return(d.baseTable.recommendations.length > 0 ? -10 : 0);
                    //return (radiusScale(d.hs_weight) <= 21 ? 0 : -10);
                }
            })
            .text(function (d) {
                return (d.baseTable["Table Name"]);// + ":" + radiusScale(d.hs_weight));
            })
            .style("cursor", "hand");

        /// Node Icons ///
        visInfoIcon = svg.selectAll(".node")
            .filter(function (d) {
                //return (radiusScale(d.hs_weight) > 21);
                return(d.baseTable.recommendations.length > 0);
            })
            .append('text')
            .classed("nodeIcon", true)
            .attr({
                "font-family": "FontAwesome",
                "font-size": "1.3em",
                "text-anchor": "middle",
                y: 10,
                //"data-powertip": "Maa ka laadla"
            })
            .text(function (d) {
                return ("\uf05a");
            })
            .style("cursor", "hand")
            .on("mouseover", function (d) {
                /*
                var popoverDesc = $(this).attr("aria-describedby");
                if (popoverDesc && popoverDesc.startsWith("popover")){
                    // popover is visible
                    return;
                }

                var htmlTable = "<table width='100%'><thead><tr><th>Type</th><th>Desc</th></tr></thead><tbody>";
                for(var index=0; index<d.baseTable.recommendations.length; index++) {
                    var r = d.baseTable.recommendations[index];
                    htmlTable += "<tr><td style='padding: 5px;vertical-align: text-top'>" + r.type + "</td><td style='padding: 5px'>" + r.desc + "</td></tr>"
                }
                htmlTable += "</tbody></table>";

                $(this).popover({
                    //title: "Recommendations",
                    title : '<span><strong>Recommendations</strong></span>'+
                    '<button type="button" id="close" class="pull-right" onclick="Visualizer.closeRecommendationPopover(this);">&times;</button>',
                    content: htmlTable,
                    html: true,
                    container: "body"
                }).popover("show");
                */

                var htmlTable = "<table width='100%'><thead><tr><th>Type</th><th>Desc</th></tr></thead><tbody>";
                for(var index=0; index<d.baseTable.recommendations.length; index++) {
                    var r = d.baseTable.recommendations[index];
                    htmlTable += "<tr><td style='padding: 5px;vertical-align: text-top'>" + r.type + "</td><td style='padding: 5px'>" + r.desc + "</td></tr>"
                }
                htmlTable += "</tbody></table>";

                $(this).data('powertip', htmlTable);
                $(this).powerTip({
                    mouseOnToPopup: true
                });
            }).on("click", function (d) {
                //$(this).popover("hide");
            });

        var tickCount = 0;

        function startForceLayout() {
            tickCount = 0;
        }

        /// The tick function ///
        function tick(e) {
            tickCount++;

            try {
                //if (nodeIsBeingDragged)
                    visNodes.each(collide(.3));

                visNodes
                    .attr("transform", function (d) {
                        // Keep the nodes within the bounds of the Viewport
                        d.x = Math.max(radiusScale(d.hs_weight) + self.nodePadding, Math.min(self.width - radiusScale(d.hs_weight) - self.nodePadding, d.x));
                        d.y = Math.max(radiusScale(d.hs_weight) + self.nodePadding, Math.min(self.height - radiusScale(d.hs_weight) - self.nodePadding, d.y));

                        // Todo: Need to figure this out, why are d.x and d.y NaNs
                        if (isNaN(d.x) && isNaN(d.y)) {
                            throw new Error("d.x and d.y are NaN");
                            //Visualizer.updateLayout(dataModel);
                            //d.px = d.x = Math.floor(Math.random() * Visualizer.width);
                            //d.py = d.y = Math.floor(Math.random() * Visualizer.height);
                        }

                        var str = "translate(" + d.x + "," + d.y + ")";
                        return (str);
                    });

                var k = .1 * e.alpha;
                var foci = [{x: 0, y: 150}, {x: 200, y: 150}];
                // Push nodes toward their designated focus.
                dataModel.nodes.forEach(function(o, i) {
                    if( o.baseTable.joins.length === 0) {
                        o.y += (foci[o.baseTable.joins.length].y - o.y) * k;
                        o.x += (foci[o.baseTable.joins.length].x - o.x) * k;
                    }
                });

                if (visLinks) {
                    visLinks.attr(
                        {
                            x1: function (d) {
                                var x = Math.max(radiusScale(d.source.hs_weight), Math.min(self.width - radiusScale(d.source.hs_weight), d.source.x));
                                return (x);
                            },
                            y1: function (d) {
                                var y = Math.max(radiusScale(d.source.hs_weight), Math.min(self.height - radiusScale(d.source.hs_weight), d.source.y));
                                return (y);
                            },
                            x2: function (d) {
                                var x = Math.max(radiusScale(d.target.hs_weight), Math.min(self.width - radiusScale(d.target.hs_weight), d.target.x));
                                return (x);
                            },
                            y2: function (d) {
                                var y = Math.max(radiusScale(d.target.hs_weight), Math.min(self.height - radiusScale(d.target.hs_weight), d.target.y));
                                return (y);
                            }
                        }
                    );

                    linkText
                        .attr("x", function(d) {
                            return ((d.source.x + d.target.x)/2);
                        })
                        .attr("y", function(d) {
                            return ((d.source.y + d.target.y)/2);
                        });

                }
            }
            catch (ex) {
                console.error(ex);
            }
        }

        function endForceLayout() {
            //console.log("Layout ended, tick count =", tickCount);
        }

        // Also check this link for collision between rectangles
        /* http://stackoverflow.com/questions/19202161/rect-collision-detection-d3js */
        // Resolves collisions between d and all other circles.
        function collide(alpha) {
            var quadtree = d3.geom.quadtree(dataModel.nodes);

            return function (d) {
                var r = radiusScale(d.hs_weight) + Visualizer.nodeMaxRadius + visCircle.nodePadding,
                    nx1 = d.x - r,
                    nx2 = d.x + r,
                    ny1 = d.y - r,
                    ny2 = d.y + r;

                quadtree.visit(function (quad, x1, y1, x2, y2) {
                    if (quad.point && (quad.point !== d)) {
                        var x = d.x - quad.point.x,
                            y = d.y - quad.point.y,
                            l = Math.sqrt(x * x + y * y),
                            r = radiusScale(d.hs_weight) + radiusScale(quad.point.hs_weight) +
                                Visualizer.nodePadding;

                        if (l < r) {
                            l = (l - r) / l * alpha;
                            x *= l;
                            d.x -= x;
                            //d.x -= x *= l;
                            y *= l;
                            d.y -= y;
                            //d.y -= y *= l;
                            quad.point.x += x;
                            quad.point.y += y;
                        }
                    }

                    return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
                });
            };
        }

        function nodeClicked(d) {
            if (d3.event.defaultPrevented)
                return;

            //console.log("clicked");
            Visualizer.lastClickedNodeData = d;
            var thisNode = d3.select(this);

            UIElements.onNodeClicked(d);

            UIElements.resetTableSelect();
        }

        function onNodeMouseOver(d) {
            if (nodeIsBeingDragged)
                return;

            var thisNode = d3.select(this);

            var linksToThisNode = dataModel.linksToNode(d);
            var nodesLinkedToThisNode = dataModel.nodesLinkedToNode(d, true);

            var visLinksToHighLight = visLine.selectAll(".link ")
                .data(linksToThisNode, uniqueID);

            var visLinkLabel =  d3.selectAll(".link-label")

            visLinkLabel.style("stroke","black").style("stroke-width",0);

            visLinkLabel.transition().duration(100)
                .style("opacity", function(o) {
                    return o.source === d || o.target === d ? 1 : .1;
                });

            visLinksToHighLight
                .attr("stroke-width", function (d) {
                    return (linkStrokeWidthScale(d.baseJoin["Join Usage Score"]));
                })
                .attr("stroke", highlightedLinkStroke)
                .attr("stroke-opacity", 1);

            visLinksToHighLight
                .exit()
                .attr("stroke-opacity", 0);

            var visCirclesToHighlight = svg.selectAll(".nodeShape")
                .data(nodesLinkedToThisNode, uniqueID);

            visCirclesToHighlight
                .exit()
                .attr("opacity", 0.25);

            var visTextToHighlight = svg.selectAll(".nodeText")
                .data(nodesLinkedToThisNode, uniqueID);

            visTextToHighlight
                .attr("font-size", "14px");

            visTextToHighlight
                .exit()
                .attr("opacity", 0.25);

            var visTextIconsToHighlight = svg.selectAll(".nodeIcon")
                .data(nodesLinkedToThisNode, uniqueID);

            visTextIconsToHighlight
                .exit()
                .attr("opacity", 0.25);

            /*thisNode
             .select("text")
             .attr("font-size", "18px");*/


            // Keep this links z-index below the node z-index (Jugaar style)
            visLine.selectAll(".link, g")
                .sort(function (a, b) {
                    if ((a instanceof Haystaxs.DataModel.LinkData && b instanceof Haystaxs.DataModel.LinkData) ||
                        (a instanceof Haystaxs.DataModel.NodeData && b instanceof Haystaxs.DataModel.NodeData)) {
                        return (0);
                    } else if (a instanceof Haystaxs.DataModel.LinkData) {
                        return (-1);
                    } else {
                        return (1);
                    }
                });

            //d.fixed = true;

            /*
             // Not needed in mouseout cos this does everything here only due to the for loop over all links
             fLayout.linkStrength(function (d) {
             for (var i = 0; i < linksToThisNode.length; i++) {
             if (d.uniqueID() === linksToThisNode[i].uniqueID()) {
             return (1);
             }
             }
             return (0);
             });
             // Not needed in mouseout cos this does everything here only due to the for loop over all nodes
             fLayout.charge(function (d) {
             for (var i = 0; i < nodesLinkedToThisNode.length; i++) {
             if (d.uniqueID() === nodesLinkedToThisNode[i].uniqueID()) {
             console.log("Charging to -100", d);
             return (-100);
             }
             }
             console.log("Charging to -30", d);
             return (-30);
             });

             fLayout.start();
             */

            fLayout.alpha(.006);
        }

        function onNodeMouseOut(d) {
            if (nodeIsBeingDragged)
                return;

            var thisNode = d3.select(this);
            //d.fixed = false;

            svg.selectAll(".link")
                .attr("stroke-width", 2)
                .attr("stroke", dimmedLinkStroke)
                .attr("stroke-opacity", dimmedLinkStrokeOpacity);

            d3.selectAll(".link-label").style("stroke","grey").style("stroke-width",0);
            d3.selectAll(".link-label").transition().duration(500)
                .style("opacity", 1);

            svg.selectAll("circle")
                .attr("opacity", 1);

            svg.selectAll(".nodeText")
                .attr("opacity", 1)
                .attr("font-size", "10px");

            svg.selectAll(".nodeIcon")
                .attr("opacity", 1);
        }

        /// Utility Functions ///
        function uniqueID(d) {
            return (d.uniqueID);
        }
    },

    /*reset: function() {
        if(fLayout)
            fLayout.stop();
    },*/

    lastClickedNodeData: null, // Contextual Variable, changes on each node click

    closeRecommendationPopover: function (closeButton) {
        $("text[aria-describedby='" + $(closeButton).closest("div[id^='popover']").attr("id") + "'").popover('hide');
    }
};
