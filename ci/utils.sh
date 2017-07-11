RESTORE='\033[0m'
RED='\033[00;31m'
GREEN='\033[00;32m'
YELLOW='\e[0;33m'

function echo_red {
    echo -e "${RED}$1${RESTORE}";
}

function echo_green {
    echo -e "${GREEN}$1${RESTORE}";
}

function echo_yellow {
    echo -e "${YELLOW}$1${RESTORE}";
}

function now_seconds {
    date +%s | cut -b1-13
}

function dorun {
    cmd="$1"
    name="$2"
    echo ----------------------------------
    echo_green "STARTING $name ..."
    echo "$cmd"
    t1=$(now_seconds)
    $cmd
    exitcode=$?
    t2=$(now_seconds)
    delta_t=$(expr $t2 - $t1)
    if [ $exitcode == 0 ]
    then
        echo_green "FINISHED $name in $delta_t s"
        echo ----------------------------------
    else
        echo_red "ERROR! $name (status: $exitcode, time: $delta_t ms)"
        echo ----------------------------------
        return $exitcode
    fi
}
