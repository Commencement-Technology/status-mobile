(ns status-im.contexts.chat.messenger.composer.utils
  (:require
    [clojure.string :as string]
    [react-native.core :as rn]
    [react-native.platform :as platform]
    [react-native.reanimated :as reanimated]
    [reagent.core :as reagent]
    [status-im.contexts.chat.messenger.composer.constants :as constants]
    [status-im.contexts.chat.messenger.composer.selection :as selection]
    [utils.number]
    [utils.re-frame :as rf]
    [utils.worklets.chat.messenger.composer :as worklets]))

(defn bounded-val
  [v min-v max-v]
  (max min-v (min v max-v)))

(defn update-height?
  [content-size height max-height]
  (let [diff (Math/abs (- content-size (reanimated/get-shared-value height)))]
    (and (not= (reanimated/get-shared-value height) max-height)
         (> diff constants/content-change-threshold))))

(defn show-top-gradient?
  [y lines max-lines gradient-opacity focused?]
  (and
   (> y constants/line-height)
   (>= lines max-lines)
   (= (reanimated/get-shared-value gradient-opacity) 0)
   @focused?))

(defn hide-top-gradient?
  [y gradient-opacity]
  (and
   (<= y constants/line-height)
   (= (reanimated/get-shared-value gradient-opacity) 1)))

(defn show-bottom-gradient?
  [{:keys [text-value focused?]} {:keys [lines]}]
  (and (not-empty @text-value) (not @focused?) (> lines 2)))

(defn show-background?
  [max-height new-height maximized?]
  (or @maximized?
      (> new-height (* constants/background-threshold max-height))))

(defn calc-lines
  [height]
  (Math/floor (/ height constants/line-height)))

(defn calc-top-content-height
  [reply? edit?]
  (cond-> 0
    reply? (+ constants/reply-container-height)
    edit?  (+ constants/edit-container-height)))

(defn calc-bottom-content-height
  [images link-previews?]
  (cond-> 0
    (seq images)   (+ constants/images-container-height)
    link-previews? (+ constants/links-container-height)))

(defn calc-reopen-height
  [text-value min-height max-height content-height saved-height]
  (if (empty? @text-value)
    min-height
    (let [input-height (min @content-height
                            (reanimated/get-shared-value saved-height))]
      (min max-height input-height))))

(defn get-min-height
  [lines]
  (if (> lines 1)
    constants/multiline-minimized-height
    constants/input-height))

(defn calc-max-height
  [{:keys [reply edit images link-previews?]} window-height kb-height insets]
  (let [margin-top (if platform/ios? (:top insets) (+ 10 (:top insets)))]
    (- window-height
       margin-top
       kb-height
       constants/bar-container-height
       constants/actions-container-height
       (calc-top-content-height reply edit)
       (calc-bottom-content-height images link-previews?))))

(defn empty-input?
  [{:keys [input-text images link-previews? reply audio edit]}]
  (not (or (not-empty input-text) images link-previews? reply audio edit)))

(defn blur-input
  [input-ref]
  (when @input-ref
    (rf/dispatch [:chat.ui/set-input-focused false])
    (.blur ^js @input-ref)))

(defn cancel-reply-message
  [input-ref]
  (js/setTimeout #(blur-input input-ref) 100)
  (rf/dispatch [:chat.ui/cancel-message-reply]))

(defn cancel-edit-message
  [text-value input-ref input-height]
  (reset! text-value "")
  ;; NOTE: adding a timeout to assure the input is blurred on the next tick
  ;; after the `text-value` was cleared. Otherwise the height will be calculated
  ;; with the old `text-value`, leading to wrong composer height after blur.
  (js/setTimeout
   (fn []
     (blur-input input-ref)
     (reanimated/set-shared-value input-height constants/input-height))
   100)
  (.setNativeProps ^js @input-ref (clj->js {:text ""}))
  (rf/dispatch [:chat.ui/set-input-content-height constants/input-height])
  (rf/dispatch [:chat.ui/cancel-message-edit]))

(defn count-lines
  [s]
  (-> s
      (string/split #"\n" -1)
      (butlast)
      count))

(defn cursor-y-position-relative-to-container
  [{:keys [scroll-y]}
   {:keys [cursor-position text-value]}]
  (let [sub-text               (subs @text-value 0 @cursor-position)
        sub-text-lines         (count-lines sub-text)
        scrolled-lines         (Math/round (/ @scroll-y constants/line-height))
        sub-text-lines-in-view (- sub-text-lines scrolled-lines)]
    (* sub-text-lines-in-view constants/line-height)))

(defn calc-suggestions-position
  [cursor-pos max-height size
   {:keys [maximized?]}
   {:keys [insets curr-height window-height keyboard-height reply edit]}
   images
   link-previews?]
  (let [base             (+ constants/composer-default-height (:bottom insets) 8)
        base             (+ base (- curr-height constants/input-height))
        base             (+ base (calc-top-content-height reply edit))
        view-height      (- window-height keyboard-height (:top insets))
        container-height (utils.number/value-in-range
                          (* (/ constants/mentions-max-height 4) size)
                          (/ constants/mentions-max-height 4)
                          constants/mentions-max-height)]
    (if @maximized?
      (if (< (+ cursor-pos container-height) max-height)
        (+ constants/actions-container-height (:bottom insets))
        (+ constants/actions-container-height (:bottom insets) (- max-height cursor-pos) 18))
      (if (< (+ base container-height) view-height)
        (let [bottom-content-height (calc-bottom-content-height images link-previews?)]
          (+ base bottom-content-height))
        (+ constants/actions-container-height (:bottom insets) (- curr-height cursor-pos) 18)))))

(defn init-non-reactive-state
  []
  {:input-ref                   (atom nil)
   :selectable-input-ref        (atom nil)
   :keyboard-show-listener      (atom nil)
   :keyboard-frame-listener     (atom nil)
   :keyboard-hide-listener      (atom nil)
   :emoji-kb-extra-height       (atom nil)
   :saved-emoji-kb-extra-height (atom nil)
   :sending-images?             (atom false)
   :sending-links?              (atom false)
   :record-reset-fn             (atom nil)
   :scroll-y                    (atom 0)
   :selection-event             (atom nil)
   :selection-manager           (rn/selectable-text-input-manager)})

(defn init-reactive-state
  []
  {:text-value            (reagent/atom "")
   :cursor-position       (reagent/atom 0)
   :saved-cursor-position (reagent/atom 0)
   :gradient-z-index      (reagent/atom 0)
   :kb-default-height     (reagent/atom 0)
   :kb-height             (reagent/atom 0)
   :gesture-enabled?      (reagent/atom true)
   :lock-selection?       (reagent/atom true)
   :focused?              (reagent/atom false)
   :lock-layout?          (reagent/atom false)
   :maximized?            (reagent/atom false)
   :record-permission?    (reagent/atom true)
   :recording?            (reagent/atom false)
   :first-level?          (reagent/atom true)
   :menu-items            (reagent/atom selection/first-level-menu-items)})

(defn init-subs
  []
  (let [chat-input (rf/sub [:chats/current-chat-input])]
    {:images                   (seq (rf/sub [:chats/sending-image]))
     :link-previews?           (or (rf/sub [:chats/link-previews?])
                                   (rf/sub [:chats/status-link-previews?]))
     :audio                    (rf/sub [:chats/sending-audio])
     :reply                    (rf/sub [:chats/reply-message])
     :edit                     (rf/sub [:chats/edit-message])
     :input-with-mentions      (rf/sub [:chat/input-with-mentions])
     :input-text               (:input-text chat-input)
     :alert-banners-top-margin (rf/sub [:alert-banners/top-margin])
     :input-content-height     (:input-content-height chat-input)}))

(defn init-shared-values
  []
  (let [composer-focused?         (reanimated/use-shared-value false)
        empty-input-shared-value? (reanimated/use-shared-value true)]
    {:composer-focused?        composer-focused?
     :empty-input?             empty-input-shared-value?
     :container-opacity        (worklets/composer-container-opacity composer-focused?
                                                                    empty-input-shared-value?
                                                                    constants/empty-opacity)
     :blur-container-elevation (worklets/blur-container-elevation composer-focused?
                                                                  empty-input-shared-value?)
     :composer-elevation       (worklets/composer-elevation composer-focused?
                                                            empty-input-shared-value?)}))

(defn init-animations
  [lines content-height max-height opacity background-y shared-values]
  (let [initial-height        (if (> lines 1)
                                constants/multiline-minimized-height
                                constants/input-height)
        bottom-content-height 0]
    (assoc shared-values
           :gradient-opacity (reanimated/use-shared-value 0)
           :height           (reanimated/use-shared-value
                              initial-height)
           :saved-height     (reanimated/use-shared-value
                              initial-height)
           :last-height      (reanimated/use-shared-value
                              (utils.number/value-in-range
                               (+ @content-height bottom-content-height)
                               constants/input-height
                               max-height))
           :opacity          opacity
           :background-y     background-y)))
