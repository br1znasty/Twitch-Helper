FROM justb4/jmeter:latest

USER root

WORKDIR /load_tests

COPY load_tests.jmx /load_tests/load_tests.jmx
COPY run.sh /load_tests/run.sh

RUN chmod +x /load_tests/run.sh

ENTRYPOINT ["/load_tests/run.sh"]