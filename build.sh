#!/bin/bash

gradle build && docker build . -t vadimdjuke/onlineshop && docker push vadimdjuke/onlineshop
