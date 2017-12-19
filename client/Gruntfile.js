module.exports = function(grunt) {
    grunt.initConfig({
        concat: {
            js: {
                dest: 'dist/bundle.js',
                src: [
                    'node_modules/jquery/dist/jquery.js',
                    'node_modules/jquery-ui-dist/jquery-ui.js',
                    'node_modules/bootstrap/dist/js/bootstrap.js',
                    'node_modules/bootstrap-select/dist/js/bootstrap-select.js',
                    'node_modules/bootstrap-select/dist/js/i18n/defaults-en_US.js',
                    'node_modules/moment/moment.js',
                    'node_modules/moment/locale/ru.js',
                    'vendor/bootstrap-filestyle.js',
                    //'node_modules/sifter/lib/sifter.js',
                    'vendor/sifter.js',
                    'node_modules/microplugin/src/microplugin.js',
                    //'node_modules/selectize/dist/js/selectize.js',
                    'vendor/selectize.js',
                    'node_modules/eonasdan-bootstrap-datetimepicker/src/js/bootstrap-datetimepicker.js',
                    //'node_modules/bootstrap-notify/bootstrap-notify.js'
                    'vendor/bootstrap-notify-fixed-for-sokol.js',
                    'node_modules/jsoneditor/dist/jsoneditor.js',
                    'node_modules/jquery-resizable/resizable.js'
                ]
            },
            appjs: {
                dest: 'dist/app.js',
                src: 'js/*.js',
                options: {
                    sourceMap: true
                }
            },
            css: {
                dest: 'dist/bundle.css',
                src: [
                    'node_modules/jquery-ui-dist/jquery-ui.css',
                    'node_modules/bootstrap/dist/css/bootstrap.css',
                    'node_modules/bootstrap-select/dist/css/bootstrap-select.css',
                    'node_modules/selectize/dist/css/selectize.css',
                    'node_modules/selectize/dist/css/selectize.bootstrap3.css',
                    'node_modules/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css',
                    'node_modules/jsoneditor/dist/jsoneditor.css',
                    'node_modules/jquery-resizable/resizable.css'
                ]
            },
            appcss: {
                dest: 'dist/app.css',
                src: 'css/*.css'
            }
        },
        copy: {
            dest: {
                files: [
                    {
                        dest: '../src/main/webapp/js/bundle.js',
                        src: ['dist/bundle.js']
                    },
                    {
                        dest: '../src/main/webapp/js/app.js',
                        src: ['dist/app.js']
                    },
                    {
                        dest: '../src/main/webapp/js/app.js.map',
                        src: ['dist/app.js.map']
                    },
                    {
                        dest: '../src/main/webapp/css/bundle.css',
                        src: ['dist/bundle.css']
                    },
                    {
                        dest: '../src/main/webapp/css/app.css',
                        src: ['dist/app.css']
                    }
                ]
            }
        }
    });


    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-copy');

    grunt.registerTask('default', ['concat']);

    grunt.registerTask('dist', ['concat', 'copy']);

};