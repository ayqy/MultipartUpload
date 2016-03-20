var express = require('express');
var fs = require('fs');
var path = require('path');

var app = express();

// static files
app.use('/images', express.static(path.join(__dirname, 'images')));
// process multipart form
app.use('/upload', require('connect-multiparty')());

app.get('/', function(req, res){
    res.send(
        '<form action="/upload" method="post" enctype="multipart/form-data">'+
        '<input type="file" name="source">'+
        '<input type="submit" value="Upload">'+
        '</form>'
    );
});

app.post('/upload', function(req, res){
    // console.log(req.body.userid);
    // console.log(req.body.username);
	console.log('Received file:\n' + JSON.stringify(req.files));
	
	var imageDir = path.join(__dirname, 'images');
	var imagePath = path.join(imageDir, req.files.source.name);
    // if exists
    fs.stat(imagePath, function(err, stat) {
        if (err && err.code !== 'ENOENT') {
            res.writeHead(500);
            res.end('fs.stat() error');
        }
        else {
            // already exists, gen a new name
            if (stat && stat.isFile()) {
                imagePath = path.join(imageDir, new Date().getTime() + req.files.source.name);
            }
            // rename
            fs.rename(
                req.files.source.path,
                imagePath,
                function(err){
                    if(err !== null){
                        console.log(err);
                        res.send({error: 'Server Writting Failed'});
                    } else {
                        res.send('ok');
                    }
                }
            );
        }
    });
});

app.get('/info', function(req, res){
    var imageNames = fs.readdirSync('images');
	res.send(imageNames);
});

app.listen(3000);