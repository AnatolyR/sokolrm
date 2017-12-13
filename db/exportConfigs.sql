SET search_path TO sokol;
COPY (SELECT path, encode(content, 'hex') FROM configs) TO :targetfolder