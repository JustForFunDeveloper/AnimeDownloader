package tapsi.logic.container;

/**
 * FINISHED: The season of the anime is finished and also all episode where downloaded
 * UNFINISHED: The season of the anime is finished but not all anime's where downloaded
 * ONAIR: The season of the anime is not finished yet
 * INFOMISSING: The necessary info for this status was not provided
 */
public enum AnimeStatus {
    FINISHED, UNFINISHED, ONAIR, INFOMISSING
}
