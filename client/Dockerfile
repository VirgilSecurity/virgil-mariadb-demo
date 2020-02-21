FROM node:13.8 as build
COPY . /client
WORKDIR /client
RUN yarn install
RUN yarn build
#USER nobody
#ENTRYPOINT ["yarn","start"]


FROM nginx:stable-alpine
RUN apk add --no-cache --update ca-certificates
COPY --from=build /client/build/ /usr/share/nginx/html/
EXPOSE 80
