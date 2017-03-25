var svg = d3.select("svg"),
    margin = {
        top: 20,
        right: 100,
        bottom: 30,
        left: 20
    },
    width = svg.attr("width") - margin.left - margin.right,
    height = svg.attr("height") - margin.top - margin.bottom,
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

var parseTime = d3.timeParse("%Y-%m-%d %H:%M:%S");

var x = d3.scaleTime().range([0, width]),
    y = d3.scaleLinear().range([height, 0]),
    z = d3.scaleOrdinal(d3.schemeCategory10);

var line = d3.line()
    .curve(d3.curveBasis)
    .x(function(d) {
        return x(d.date);
    })
    .y(function(d) {
        return y(d.v);
    });

function loadData(data) {
    for (var i = 0; i < data.length; i++) {
        data[i].date = parseTime(data[i].date)
    }
    if (data.length > 0){
        data.columns = []
        for (var k in data[0]) {
            data.columns.push(k)
        }
    }
    var ms = data.columns.slice(1).map(function(id) {
        return {
            id: id,
            values: data.map(function(d) {
                return {
                    date: d.date,
                    v: d[id]
                };
            })
        };
    });

    x.domain(d3.extent(data, function(d) {
        return d.date;
    }));

    y.domain([
        d3.min(ms, function(c) {
            return d3.min(c.values, function(d) {
                return d.v;
            });
        }),
        d3.max(ms, function(c) {
            return d3.max(c.values, function(d) {
                return d.v;
            });
        })
    ]);

    z.domain(ms.map(function(c) {
        return c.id;
    }));

    g.append("g")
        .attr("stroke-width", 4)
        .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    g.append("g")
        .attr("stroke-width", 4)
        .attr("class", "axis axis--y")
        .call(d3.axisLeft(y))
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 30)
        .attr("dy", "1em")
        .attr("fill", "#000")
        .text(""); //TODO

    var mm = g.selectAll(".mm")
        .attr("stroke-width", 4)
        .data(ms)
        .enter().append("g")
        .attr("class", "mm");

    mm.append("path")
        .attr("stroke-width", 4)
        .attr("class", "line")
        .attr("d", function(d) {
            return line(d.values);
        })
        .style("stroke", function(d) {
            return z(d.id);
        });

    mm.append("text")
        .datum(function(d) {
            return {
                id: d.id,
                value: d.values[d.values.length - 1]
            };
        })
        .attr("transform", function(d) {
            return "translate(" + x(d.value.date) + "," + y(d.value.v) + ")";
        })
        .attr("x", 3)
        .attr("dy", "1em")
        .style("font", "16px sans-serif")
        .text(function(d) {
            return d.id;
        });
}
