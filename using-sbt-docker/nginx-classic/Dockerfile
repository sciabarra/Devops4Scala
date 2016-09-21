FROM alpine
RUN apk update &&\
    apk add nginx &&\
	echo "daemon off;" >>/etc/nginx/nginx.conf &&\
	mkdir /run/nginx
COPY index.html /var/lib/nginx/html/index.html
CMD /usr/sbin/nginx

