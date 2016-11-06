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
                    'node_modules/bootstrap-select/dist/js/i18n/defaults-en_US.js'
                ]
            },
            css: {
                dest: 'dist/bundle.css',
                src: [
                    'node_modules/jquery-ui-dist/jquery-ui.css',
                    'node_modules/bootstrap/dist/css/bootstrap.css',
                    'node_modules/bootstrap-select/dist/css/bootstrap-select.css'
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