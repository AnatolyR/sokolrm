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
                    'node_modules/sifter/lib/sifter.js',
                    'node_modules/microplugin/src/microplugin.js',
                    'node_modules/selectize/dist/js/selectize.js',
                    'node_modules/eonasdan-bootstrap-datetimepicker/src/js/bootstrap-datetimepicker.js',
                    'node_modules/bootstrap-notify/bootstrap-notify.js'
                ]
            },
            app: {
                dest: 'dist/app.js',
                src: 'js/*.js'
            },
            css: {
                dest: 'dist/bundle.css',
                src: [
                    'node_modules/jquery-ui-dist/jquery-ui.css',
                    'node_modules/bootstrap/dist/css/bootstrap.css',
                    'node_modules/bootstrap-select/dist/css/bootstrap-select.css',
                    'node_modules/selectize/dist/css/selectize.css',
                    'node_modules/selectize/dist/css/selectize.bootstrap3.css',
                    'node_modules/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.css'
                ]
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
                        dest: '../src/main/webapp/css/bundle.css',
                        src: ['dist/bundle.css']
                    }
                ]
            }
        }
    });


    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-copy');

    grunt.registerTask('default', ['concat']);

};