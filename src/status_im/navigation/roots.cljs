(ns status-im.navigation.roots
  (:require
    [quo.foundations.colors :as colors]
    [status-im.constants :as constants]
    [status-im.navigation.options :as options]))

;; Theme Order for navigation roots
;; 1. Themes hardcoded in below map
;; 2. If nil or no entry in map, then theme stored in
;;    [:db :profile/profile :appearance] will be used (for mulitaccounts)
;; 3). Fallback theme - Dark
(def themes
  {:screen/onboarding.intro constants/theme-type-dark
   :screen/profile.profiles constants/theme-type-dark
   :shell-stack             nil})

(defn roots-internal
  [status-bar-theme]
  {:screen/onboarding.intro
   {:root
    {:stack {:id       :screen/onboarding.intro
             :children [{:component {:name    :screen/onboarding.intro
                                     :id      :screen/onboarding.intro
                                     :options (options/dark-root-options)}}]}}}
   :shell-stack
   {:root
    {:stack {:id       :shell-stack
             :children [{:component {:name    :shell-stack
                                     :id      :shell-stack
                                     :options (options/root-options
                                               {:nav-bar-color    colors/neutral-100
                                                :status-bar-theme status-bar-theme})}}]}}}
   :screen/profile.profiles
   {:root
    {:stack {:id       :screen/profile.profiles
             :children [{:component {:name    :screen/profile.profiles
                                     :id      :screen/profile.profiles
                                     :options (options/dark-root-options)}}]}}}

   :screen/onboarding.enable-notifications
   {:root {:stack {:children [{:component {:name    :screen/onboarding.enable-notifications
                                           :id      :screen/onboarding.enable-notifications
                                           :options (options/dark-root-options)}}]}}}

   :screen/onboarding.welcome
   {:root {:stack {:children [{:component {:name    :screen/onboarding.welcome
                                           :id      :screen/onboarding.welcome
                                           :options (options/dark-root-options)}}]}}}
   :screen/onboarding.syncing-results
   {:root {:stack {:children [{:component {:name    :screen/onboarding.syncing-results
                                           :id      :screen/onboarding.syncing-results
                                           :options (options/dark-root-options)}}]}}}})

(defn old-roots
  [status-bar-theme]
  {:progress
   {:root {:stack {:children [{:component {:name    :progress
                                           :id      :progress
                                           :options (options/root-options {:status-bar-theme
                                                                           status-bar-theme})}}]
                   :options  (assoc (options/root-options nil)
                                    :topBar
                                    {:visible false})}}}})

(defn roots
  [status-bar-theme]
  (merge
   (old-roots status-bar-theme)
   (roots-internal status-bar-theme)))
