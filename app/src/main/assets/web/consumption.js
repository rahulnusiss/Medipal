var width = 450,
    height = 420,
    cellSize = 12;

var no_months_in_a_row = Math.floor(width / (cellSize * 7 + 50));
var shift_up = cellSize * 3;

var day = d3.time.format("%w"),
    day_of_month = d3.time.format("%e")
day_of_year = d3.time.format("%j")
week = d3.time.format("%U"),
    month = d3.time.format("%m"),
    year = d3.time.format("%Y"),
    percent = d3.format(".1%"),
    format = d3.time.format("%Y-%m-%d");

var svg = d3.select("#chart").selectAll("svg")
    .data(d3.range(2017, 2018))
    .enter().append("svg")
    .attr("width", width)
    .attr("height", height)
    .attr("class", "RdYlGn")
    .append("g")

var rect = svg.selectAll(".day")
    .data(function(d) {
        return d3.time.days(new Date(d, 0, 1), new Date(d + 1, 0, 1));
    })
    .enter().append("rect")
    .attr("class", "day")
    .attr("width", cellSize)
    .attr("height", cellSize)
    .attr("x", function(d) {
        var month_padding = 1.2 * cellSize * 7 * ((month(d) - 1) % (no_months_in_a_row));
        return day(d) * cellSize + month_padding;
    })
    .attr("y", function(d) {
        var week_diff = week(d) - week(new Date(year(d), month(d) - 1, 1));
        var row_level = Math.ceil(month(d) / (no_months_in_a_row));
        return (week_diff * cellSize) + row_level * cellSize * 8 - cellSize / 2 - shift_up;
    })
    .datum(format);

var month_titles = svg.selectAll(".month-title")
    .data(function(d) {
        return d3.time.months(new Date(d, 0, 1), new Date(d + 1, 0, 1));
    })
    .enter().append("text")
    .text(monthTitle)
    .attr("x", function(d, i) {
        var month_padding = 1.2 * cellSize * 7 * ((month(d) - 1) % (no_months_in_a_row));
        return month_padding;
    })
    .attr("y", function(d, i) {
        var week_diff = week(d) - week(new Date(year(d), month(d) - 1, 1));
        var row_level = Math.ceil(month(d) / (no_months_in_a_row));
        return (week_diff * cellSize) + row_level * cellSize * 8 - cellSize - shift_up;
    })
    .attr("class", "month-title")
    .attr("d", monthTitle);

var year_titles = svg.selectAll(".year-title")
    .data(function(d) {
        return d3.time.years(new Date(d, 0, 1), new Date(d + 1, 0, 1));
    })
    .enter().append("text")
    .text(yearTitle)
    .attr("x", function(d, i) {
        return width / 2 - 100;
    })
    .attr("y", function(d, i) {
        return cellSize * 5.5 - shift_up;
    })
    .attr("class", "year-title")
    .attr("d", yearTitle);

function loadData(csv) {
    var data = d3.nest()
        .key(function(d) {
            return d.Date;
        })
        .rollup(function(d) {
            var sum = 0;
            for (var i = 0; i < d.length; i++) {
              sum += d[i].Quantity
            }
            return sum;
        })
        .map(csv);

    rect.filter(function(d) {
            return d in data;
        })
        .attr("class", function(d) {
            return "day q" + data[d] + "-11";
        })
        .select("title")
        .text(function(d) {
            return d + ": " + percent(data[d]);
        });

}

function dayTitle(t0) {
    return t0.toString().split(" ")[2];
}

function monthTitle(t0) {
    return t0.toLocaleString("en-us", { month: "long" });
}

function yearTitle(t0) {
    return t0.toString().split(" ")[3];
}
