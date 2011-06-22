var drawDRMap = function(vals, bins, context_id, step)
{
    var maxval = 0;
    for each (var val in vals) {
        var lval = Math.log(val);
        if (lval > maxval) {
            maxval = lval;
        }
    }

    var elem = document.getElementById(context_id);


    if (elem && elem.getContext) {
        var context = elem.getContext('2d');
        if (context) {
            var side = elem.width / bins;

            var i = bins * step;
            for (x = 0; x < bins; x++) {
                for (y = bins - 1; y >= 0; y--) {
                    if (vals[i] == 0) {
                        context.fillStyle = 'rgba(0, 0, 255, 1)';
                    }
                    else {
                        context.fillStyle = 'rgba(' + Math.floor(vals[i] * 255.0) + ', 0, 0, 1)';
                    }
                    context.fillRect(x * side, y * side, side, side);
                    i++;
                }
            }
        
            context.strokeStyle = '#0F0';
            context.lineWidth = 1.0;
            context.beginPath();
            context.moveTo(0, elem.height / 2);
            context.lineTo(elem.width, elem.height / 2);
            context.stroke();
            context.beginPath();
            context.moveTo(elem.width / 2, 0);
            context.lineTo(elem.width / 2, elem.height);
            context.stroke();
        }
    }
}
