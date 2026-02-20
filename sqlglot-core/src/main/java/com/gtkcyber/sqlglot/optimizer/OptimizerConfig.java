/*
 * MIT License
 *
 * Copyright (c) 2026 GTK Cyber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gtkcyber.sqlglot.optimizer;

/**
 * Configuration for the optimizer with feature flags for individual rules.
 *
 * Phase 5A (Core Rules):
 * @param simplify enables SimplifyRule (boolean/arithmetic simplification)
 * @param canonicalize enables CanonicalizeRule (expression normalization)
 * @param quoteIdentifiers enables QuoteIdentifiersRule (dialect-specific quoting)
 * @param eliminateCtes enables EliminateCTEsRule (unused CTE elimination)
 *
 * Phase 5B (Advanced Rules):
 * @param normalizePredicates enables NormalizePredicatesRule (boolean normalization)
 * @param pushdownPredicates enables PushdownPredicatesRule (WHERE clause pushdown)
 * @param mergeSubqueries enables MergeSubqueriesRule (subquery inlining)
 * @param joinReordering enables JoinReorderingRule (join optimization)
 * @param projectionPushdown enables ProjectionPushdownRule (column pruning)
 * @param annotateTypes enables AnnotateTypesRule (type inference)
 * @param qualifyColumns enables QualifyColumnsRule (column qualification)
 */
public record OptimizerConfig(
        boolean simplify,
        boolean canonicalize,
        boolean quoteIdentifiers,
        boolean eliminateCtes,
        boolean normalizePredicates,
        boolean pushdownPredicates,
        boolean mergeSubqueries,
        boolean joinReordering,
        boolean projectionPushdown,
        boolean annotateTypes,
        boolean qualifyColumns
) {
    /**
     * Default configuration - Phase 5A rules enabled, Phase 5B disabled
     */
    public static OptimizerConfig DEFAULT = new OptimizerConfig(
            true,   // simplify
            true,   // canonicalize
            true,   // quoteIdentifiers
            true,   // eliminateCtes
            false,  // normalizePredicates (Phase 5B)
            false,  // pushdownPredicates (Phase 5B)
            false,  // mergeSubqueries (Phase 5B)
            false,  // joinReordering (Phase 5B)
            false,  // projectionPushdown (Phase 5B)
            false,  // annotateTypes (Phase 5B)
            false   // qualifyColumns (Phase 5B)
    );

    /**
     * Minimal configuration - only simplify
     */
    public static OptimizerConfig MINIMAL = new OptimizerConfig(
            true,   // simplify
            false,  // canonicalize
            false,  // quoteIdentifiers
            false,  // eliminateCtes
            false,  // normalizePredicates
            false,  // pushdownPredicates
            false,  // mergeSubqueries
            false,  // joinReordering
            false,  // projectionPushdown
            false,  // annotateTypes
            false   // qualifyColumns
    );

    /**
     * Phase 5A configuration - core optimization rules only
     */
    public static OptimizerConfig PHASE_5A = new OptimizerConfig(
            true,   // simplify
            true,   // canonicalize
            true,   // quoteIdentifiers
            true,   // eliminateCtes
            false,  // normalizePredicates
            false,  // pushdownPredicates
            false,  // mergeSubqueries
            false,  // joinReordering
            false,  // projectionPushdown
            false,  // annotateTypes
            false   // qualifyColumns
    );

    /**
     * Phase 5B configuration - all Phase 5A + Phase 5B rules enabled
     */
    public static OptimizerConfig PHASE_5B = new OptimizerConfig(
            true,   // simplify
            true,   // canonicalize
            true,   // quoteIdentifiers
            true,   // eliminateCtes
            true,   // normalizePredicates
            true,   // pushdownPredicates
            true,   // mergeSubqueries
            true,   // joinReordering
            true,   // projectionPushdown
            true,   // annotateTypes
            true    // qualifyColumns
    );

    /**
     * Aggressive configuration - all available rules enabled (same as PHASE_5B)
     */
    public static OptimizerConfig AGGRESSIVE = PHASE_5B;
}
