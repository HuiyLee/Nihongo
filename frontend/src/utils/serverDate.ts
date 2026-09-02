/**
 * The backend serializes java.time.LocalDateTime fields (startedAt,
 * submittedAt, etc.) as zone-less ISO strings, e.g. "2026-09-02T03:15:00" -
 * no trailing "Z" or "+hh:mm". Those values are actually UTC instants (the
 * JVM on Render runs in UTC), but the JS `Date` constructor treats an
 * offset-less ISO date-time string as *local time in the browser's own
 * timezone* (this is standard, spec-mandated Date-parsing behaviour, not a
 * bug in any particular browser).
 *
 * For a user in Vietnam (UTC+7) that silently shifts every such timestamp
 * 7 hours into the future from what it actually is. That's barely
 * noticeable on a plain ".toLocaleString()" display, but it's fatal for
 * real time-math: ExamAttemptPage computes `deadline = startedAt +
 * durationMinutes`, and comparing that against `Date.now()` (which *is*
 * correctly UTC-based) after the fact makes the deadline look like it's
 * already hours in the past - so the countdown reads 00:00 immediately and
 * the page auto-submits the exam before the learner has answered anything.
 *
 * Fix: parse these fields as UTC explicitly, by appending "Z" whenever the
 * string doesn't already carry a zone/offset designator.
 */
export function parseServerDateTime(value: string): Date {
  const hasZoneDesignator = /[zZ]|[+-]\d{2}:\d{2}$/.test(value);
  return new Date(hasZoneDesignator ? value : `${value}Z`);
}
